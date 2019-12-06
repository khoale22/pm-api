package com.heb.pm.core.service;

import com.heb.pm.core.repository.PhSubCommodityRepository;
import com.heb.pm.dao.core.entity.*;
import com.heb.pm.dao.core.entity.codes.DescriptionType;
import com.heb.pm.dao.core.entity.codes.ExtendedAttributeType;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

/**
 * Handles processing candidate products.
 *
 * @author d116773
 * @since 1.1.0
 */
@Service
public class CandidateProductService {

	@Value("${validation.enabled.romanceCopy}")
	private transient boolean validationEnabledRomanceCopy;

	private static final String SCANNABLE_DEFAULT = "N";

	private static final String PSE_DEFAULT = ",";

	private static final long PRE_PRICE_XFOR_DEFAULT = 0;
	private static final long XFOR_DEFAULT = 1L;

	private static final int MAX_SCAN_DESCRIPTION_SIZE = 12;

	private static final long SCALE_DEFAULT_VALUE = 0;

	@Autowired
	private transient PhSubCommodityRepository phSubCommodityRepository;

	/**
	 * Converts a CandidateProduct to a CandidateProductMaster.
	 *
	 * @param user The ID of the user who kicked off this request.
	 * @param candidateWorkRequest This candidate work request the CandidateProductMaster will be a part of
	 * @param candidate The Candidate that kicked off this request.
	 * @param candidateProduct The CandidateProduct being worked on.
	 * @return The converted CandidateProduct.
	 */
	public CandidateProductMaster buildCandidateProductMaster(String user, CandidateWorkRequest candidateWorkRequest,
															  Candidate candidate, CandidateProduct candidateProduct) {

		// Get the sub-commodity, it'll be needed for setting some of these values.
		// The validation would have failed if the sub-commodity is not good.
		PhSubCommodity subCommodity = this.phSubCommodityRepository.findByKeySubCommodityCode(candidate.getSubCommodity().getSubCommodityId()).get(0);

		// Get the CandidateProductMaster level data.
		CandidateProductMaster candidateProductMaster = this.candidateProductToCandidateProductMaster(user, candidateWorkRequest, candidate, candidateProduct, subCommodity);

		// Add the objects that'll be tied to the CandidateProductMaster.
		candidateProductMaster.getCandidateSellingUnits()
				.add(this.candidateProductMasterToCandidateSellingUnits(user, candidate, candidateProduct, candidateProductMaster));
		this.handleTobaccoFields(candidateProductMaster);
		this.handleRetailFields(candidate, candidateProductMaster);
		this.handleScaleFields(candidateProductMaster);

		candidateProductMaster.getCandidateProductDescriptions().add(this.getStandardCandidateProductDescription(candidateProduct, candidateProductMaster));
		candidateProductMaster.getCandidateProductDescriptions().add(this.getCustomerFriendlyDescriptionOne(candidateProduct, candidateProductMaster));
		this.getCustomerFriendlyDescriptionTwo(candidateProduct, candidateProductMaster).ifPresent(candidateProductMaster.getCandidateProductDescriptions()::add);

		return candidateProductMaster;
	}

	/**
	 * Converts a CandidateProduct to a CandidateProductMaster. This is pulled out to make testing easier.
	 *
	 * @param user The ID of the user who kicked off this request.
	 * @param candidateWorkRequest This candidate work request the CandidateProductMaster will be a part of
	 * @param candidate The Candidate that kicked off this request.
	 * @param candidateProduct The CandidateProduct being worked on.
	 * @return The converted CandidateProduct.
	 */
	protected CandidateProductMaster candidateProductToCandidateProductMaster(String user, CandidateWorkRequest candidateWorkRequest,
										Candidate candidate, CandidateProduct candidateProduct,
										PhSubCommodity subCommodity) {

		String productDescription = candidateProduct.getDescription().trim().toUpperCase(Locale.US);

		CandidateProductMaster candidateProductMaster = new CandidateProductMaster();
		candidateProductMaster.setDescription(productDescription)
				.setCandidateWorkRequest(candidateWorkRequest)
				.setClassCode(subCommodity.getCommodity().getKey().getClassCode())
				.setCommodityId(subCommodity.getCommodity().getKey().getCommodityCode())
				.setSubCommodityId(subCommodity.getKey().getSubCommodityCode())
				.setBdmId(candidate.getBuyer().getBuyerId())
				.setLastUpdateUserId(user)
				.setProductType(CandidateProductMaster.PRODUCT_TYPE_BASIC)
				.setQuantityRestricted(CandidateUtils.NO)
				.setAvc(CandidateUtils.NO)
				.setStk(CandidateUtils.NO)
				.setSpecSheetAttached(CandidateUtils.NO)
				.setFoodStampEligible(CandidateUtils.booleanToSwitch(candidate.getFoodStamp()))
				.setTaxable(CandidateUtils.booleanToSwitch(candidate.getTaxable()))
				.setSeasonal(CandidateUtils.NO)
				.setPharmacyProduct(CandidateUtils.NO)
				.setAlcoholProduct(CandidateUtils.NO)
				.setGenericProduct(CandidateUtils.NO)
				.setPrivateLabelProduct(CandidateUtils.NO)
				.setMacEac(CandidateUtils.NO)
				.setScannable(SCANNABLE_DEFAULT)
				.setFsa(CandidateUtils.booleanToSwitch(candidate.getFlexibleSpendingAccount()))
				.setPseType(PSE_DEFAULT)
				.setHealthyLivingProduct(CandidateUtils.NO)
				.setDsdDeleted(CandidateUtils.NO)
				.setActivate(CandidateUtils.YES)
				.setDsdDepartment(CandidateUtils.NO)
				.setDrugFactPanel(CandidateUtils.NO)
				.setEas(CandidateUtils.NO)
				.setIngredient(CandidateUtils.NO)
				.setWicable(CandidateUtils.NO)
				.setSelfManufactured(CandidateUtils.NO)
				.setBrand(candidate.getBrand().getBrandId())
				.setPrimaryUpc(candidateProduct.getUpc())
				.setNewData(CandidateUtils.YES)
				.setPssDepartment(subCommodity.getCommodity().getPssDepartment())
				.setScanDescription(productDescription.length() <= MAX_SCAN_DESCRIPTION_SIZE ?
						productDescription :  productDescription.substring(0, MAX_SCAN_DESCRIPTION_SIZE).trim())
				.setVertexTaxCategory(CandidateUtils.vertexTaxCategoryFromTaxableAndSubCommodity(candidate.getTaxable(), subCommodity))
				.setPackageType(org.apache.commons.lang3.StringUtils.truncate(candidate.getPackageType().getDescription(), 30).toUpperCase(Locale.US).trim())
		;

		return candidateProductMaster;
	}

	/**
	 * Converts a CandidateProduct to a CandidateSellingUnit.
	 *
	 * @param candidate The Candidate that kicked off this request.
	 * @param candidateProduct The CandidateProduct being worked on.
	 * @param candidateProductMaster The CandidateProductMaster the CandidateSellingUnit will be added to.
	 * @return
	 */
	protected CandidateSellingUnit candidateProductMasterToCandidateSellingUnits(
			String user, Candidate candidate,
			CandidateProduct candidateProduct,
			CandidateProductMaster candidateProductMaster) {

		CandidateSellingUnit candidateSellingUnit = new CandidateSellingUnit();

		// Right now, we only have one UPC per candidate.
		candidateSellingUnit.getKey().setSequenceNumber(1L);

		candidateSellingUnit.setCandidateProductMaster(candidateProductMaster)
				.setUpc(candidateProduct.getUpc())
				.setScanCodeType(SellingUnit.SCAN_TYPE_UPC)
				.setBonus(CandidateUtils.NO)
				.setLastUpdatedBy(user)
				.setLastUpdatedOn(Instant.now())
				.setTestScanOverride(CandidateUtils.YES)
				.setTestScanUpc(this.getUpcWithCheckDigit(candidateProduct.getUpc(),
						candidateProduct.getUpcCheckDigit()))
				.setSellingSizeOne(candidate.getTotalVolume())
				.setSellingSizeCodeOne(candidate.getUnitOfMeasure().getUnitOfMeasureId())
				.setNewData(CandidateUtils.YES)
				.setHeight(candidate.getProductHeight())
				.setLength(candidate.getProductLength())
				.setWidth(candidate.getProductWidth())
				.setWeight(candidate.getProductWeight())
				.setSampleProvided(CandidateUtils.NO)
				.setTagSize(candidate.getRetailSize());

		addCandidateSellingUnitExtendedAttributeToCandidateSellingUnit(candidateProduct, candidateSellingUnit);

		return candidateSellingUnit;
	}

	/**
	 * Add CandidateSellingUnitExtendedAttribute to CandidateSellingUnit.
	 * @param candidateProduct to get info from.
	 * @param candidateSellingUnit to add info to.
	 */
	private void addCandidateSellingUnitExtendedAttributeToCandidateSellingUnit(
			CandidateProduct candidateProduct, CandidateSellingUnit candidateSellingUnit) {

		if (validationEnabledRomanceCopy) {
			CandidateSellingUnitExtendedAttribute candidateSellingUnitExtendedAttribute =
					CandidateSellingUnitExtendedAttribute.of(candidateSellingUnit, ExtendedAttributeType.ROMANCE_COPY)
							.setDescription(candidateProduct.getRomanceCopy());

			candidateSellingUnit.getCandidateSellingUnitExtendedAttributes().add(candidateSellingUnitExtendedAttribute);
		}
	}

	/**
	 * Returns a UPC with the check digit attached.
	 *
	 * @param upc The UPC.
	 * @param checkDigit The check digit.
	 * @return The UPC with the check digit attached.
	 */
	protected Long getUpcWithCheckDigit(Long upc, Long checkDigit) {

		String toConvert = String.format("%d%d", upc, checkDigit);
		return Long.valueOf(toConvert);
	}

	/**
	 * Returns the standard product description.
	 *
	 * @param candidateProduct The candidate product being processed.
	 * @param candidateProductMaster The CandidateProductMaster being create
	 * @return The standard product description.
	 */
	protected CandidateProductDescription getStandardCandidateProductDescription(CandidateProduct candidateProduct, CandidateProductMaster candidateProductMaster) {

		CandidateProductDescription candidateProductDescription = new CandidateProductDescription();
		candidateProductDescription.getKey().setLanguageType(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH)
				.setDescriptionType(DescriptionType.PRODUCT_DESCRIPTION);

		candidateProductDescription.setCandidateProductMaster(candidateProductMaster)
				.setDescription(candidateProduct.getDescription().trim().toUpperCase(Locale.US))
				.setLastUpdateDate(Instant.now()).setLastUpdateUserId(candidateProductMaster.getLastUpdateUserId());

		return candidateProductDescription;
	}

	/**
	 * Returns the customer friendly description line one.
	 *
	 * @param candidateProduct The candidate product being processed.
	 * @param candidateProductMaster The CandidateProductMaster being create
	 * @return The customer friendly description line one.
	 */
	protected CandidateProductDescription getCustomerFriendlyDescriptionOne(CandidateProduct candidateProduct, CandidateProductMaster candidateProductMaster) {

		CandidateProductDescription candidateProductDescription = new CandidateProductDescription();
		candidateProductDescription.getKey().setLanguageType(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH)
				.setDescriptionType(DescriptionType.CFD_1);

		candidateProductDescription.setCandidateProductMaster(candidateProductMaster)
				.setDescription(candidateProduct.getCustomerFriendlyDescription1())
				.setLastUpdateDate(Instant.now()).setLastUpdateUserId(candidateProductMaster.getLastUpdateUserId());

		return candidateProductDescription;
	}

	/**
	 * Returns an optional customer friendly description line two. This may be empty.
	 *
	 * @param candidateProduct The candidate product being processed.
	 * @param candidateProductMaster The CandidateProductMaster being create
	 * @return The customer friendly description line two or empty.
	 */
	protected Optional<CandidateProductDescription> getCustomerFriendlyDescriptionTwo(CandidateProduct candidateProduct, CandidateProductMaster candidateProductMaster) {

		if (!StringUtils.hasText(candidateProduct.getCustomerFriendlyDescription2())) {
			return Optional.empty();
		}

		CandidateProductDescription candidateProductDescription = new CandidateProductDescription();
		candidateProductDescription.getKey().setLanguageType(ProductDescriptionKey.LANGUAGE_TYPE_ENGLISH)
				.setDescriptionType(DescriptionType.CFD_2);

		candidateProductDescription.setCandidateProductMaster(candidateProductMaster)
				.setDescription(candidateProduct.getCustomerFriendlyDescription2())
				.setLastUpdateDate(Instant.now()).setLastUpdateUserId(candidateProductMaster.getLastUpdateUserId());

		return Optional.of(candidateProductDescription);
	}

	/**
	 * Processes the tobacco fields of the product. The CandidateProductMaster will be modified.
	 *
	 * @param candidateProductMaster The CandidateProductMaster being created.
	 */
	protected void handleTobaccoFields(CandidateProductMaster candidateProductMaster) {

		// We don't support tobacco yet.
		candidateProductMaster.setTobaccoProduct(CandidateUtils.NO)
				.setTobaccoType(CandidateProductMaster.TOBACCO_TYPE_NOT_TOBACCO)
				.setTobaccoUnstamped(CandidateUtils.NO);
	}

	/**
	 * Processes the retail fields of the product. The CandidateProductMaster will be modified.
	 *
	 * @param candidate The candidate being processed.
	 * @param candidateProductMaster The CandidateProductMaster being created.
	 */
	protected void handleRetailFields(Candidate candidate, CandidateProductMaster candidateProductMaster) {

		candidateProductMaster.setPrePriced(CandidateUtils.NO)
				.setPrePriceXFor(PRE_PRICE_XFOR_DEFAULT)
				.setMatrixMargin(CandidateUtils.NO);

		switch (candidate.getRetailType()) {
			case Candidate.RETAIL_LINK:
				candidateProductMaster.setMarkPriceInStore(CandidateUtils.NO)
						.setPriceRequired(CandidateUtils.NO)
						.setRetailLinkId(candidate.getRetailLink().getRetailLinkNumber())
						.setRetailLinkUpc(candidate.getRetailLink().getUnitUpc())
						.setXFor(XFOR_DEFAULT);
				break;
			case Candidate.PRICE_REQUIRED:
				candidateProductMaster.setMarkPriceInStore(CandidateUtils.NO)
						.setPriceRequired(CandidateUtils.YES)
						.setXFor(XFOR_DEFAULT);
				break;
			default:
				candidateProductMaster.setMarkPriceInStore(CandidateUtils.NO)
						.setXFor(candidate.getRetailXFor()).
						setRetail(candidate.getRetailPrice())
						.setPriceRequired(CandidateUtils.NO);
				break;
		}
	}

	/**
	 * Processes the scale fields of the product. The CandidateProductMaster will be modified.
	 *
	 * @param candidateProductMaster The CandidateProductMaster being created.
	 */
	protected void handleScaleFields(CandidateProductMaster candidateProductMaster) {

		// We don't handle scale product  yet.
		candidateProductMaster.setSoldByWeight(CandidateUtils.NO)
				.setScaleProduct(CandidateUtils.NO)
				.setStrip(CandidateUtils.YES)
				.setForceTare(CandidateUtils.NO)
				.setScaleActionCode(SCALE_DEFAULT_VALUE)
				.setScaleGrahicsCode(SCALE_DEFAULT_VALUE)
				.setScaleLabelFormatOne(SCALE_DEFAULT_VALUE)
				.setScaleLabelFormatTwo(SCALE_DEFAULT_VALUE);
	}

}
