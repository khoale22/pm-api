package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.repository.CandidateTransactionRepository;
import com.heb.pm.core.repository.CostOwnerRepository;
import com.heb.pm.core.repository.PhSubCommodityRepository;
import com.heb.pm.core.repository.VendorLocationRepository;
import com.heb.pm.dao.core.entity.codes.DescriptionType;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import com.heb.pm.pam.model.Lane;
import com.heb.pm.pam.model.Warehouse;
import com.heb.pm.util.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Pulls candidate validation into a separate class to keep the service at a resonable size.
 *
 * @author d116773
 * @since 1.1.0
 */
@Service
@SuppressWarnings("PMD.GodClass")
public class CandidateValidator {

	@Value("${validation.enabled.romanceCopy}")
	private transient boolean validationEnabledRomanceCopy;

	public static final int MAX_LENGTH_ROMANCE_COPY = 4000;

	@Autowired
	private transient PhSubCommodityRepository phSubCommodityRepository;

	@Autowired
	private transient VendorLocationRepository vendorLocationRepository;

	@Autowired
	private transient CandidateTransactionRepository candidateTransactionRepository;

	@Autowired
	private transient CostOwnerRepository costOwnerRepository;

	private static final Long COST_OWNER_UNASSIGNED = 0L;
	private static final Long BRAND_UNASSIGNED = 0L;
	private static final Long TOP_TO_TOP_UNASSIGNED = 0L;
	private static final Long COUNTRY_UNASSIGNED = 0L;
	private static final BigDecimal MINIMUM_REQUIRED_RETAIL = BigDecimal.ZERO;
	private static final Long MINIMUM_REQUIRED_XFOR = 1L;

	private static final BigDecimal MAX_CUBE = new BigDecimal("99999.9999");
	private static final BigDecimal MAX_SHIP_CUBE = new BigDecimal("999999.999");

	private static final int MAX_RETAIL_SIZE = 6;
	private static final String ITEM_CLASS_DEPARTMENT_AS_STRING_CONVERSION = "%02d";
	private static final List<String> ITEM_WEIGHT_DEPT_LIST = new ArrayList<>(Arrays.asList("02", "06", "09"));

	/**
	 * This is hinkey, but, since I want to save this in a different transaction than when things are going fine,
	 * I had to move this to a different class.
	 *
	 * @param candidateTransaction The CandidateTransaction to save.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveErrorTransaction(CandidateTransaction candidateTransaction) {

		this.candidateTransactionRepository.save(candidateTransaction);
	}

	/**
	 * Validates a candidate. It will return a list of errors associated with the candidate. This list will always exist
	 * but may be empty if there are no errors. It will modify the candidate with looked-up values where appropriate for
	 * effeciency.
	 *
	 * @param candidate The Candidate to validate. The candidate will be modified.
	 * @return A list of errors. IF the list is empty, there will be no errors.
	 */
	public List<String> validate(Candidate candidate) {

		List<String> errors = new LinkedList<>();

		try {

			ValidatorUtils.validateFieldExists(candidate::getDescription, "EACT-001: Candidate description is required.").ifPresent(errors::add);

			this.validateCommodityAndSubCommodity(candidate).ifPresent(errors::add);
			this.validateRetail(candidate).ifPresent(errors::add);
			this.validateCost(candidate).ifPresent(errors::add);
			this.validateRetailSize(candidate).ifPresent(errors::add);

			ValidatorUtils.validateFieldExists(candidate::getBrand, "EACT-004: Brand is required").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getBuyer, "EACT-005: Buyer is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getUnitOfMeasure, "EACT-007: Unit of measure is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getInnerLength, "EACT-008: Case inner length is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getInnerWidth, "EACT-009: Case inner width is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getInnerHeight, "EACT-010: Case inner height is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getInnerWeight, "EACT-012: Case inner weight is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getInnerPack, "EACT-013: Inner pack is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getMasterLength, "EACT-014: Case master length is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getMasterWidth, "EACT-015: Case master width is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getMasterHeight, "EACT-016: Case master height is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getMasterWeight, "EACT-017: Case master weight is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getMasterPack, "EACT-019: Master pack is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getProductLength, "EACT-020: Product length is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getProductWidth, "EACT-021: Product width is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getProductHeight, "EACT-022: Product height is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getProductWeight, "EACT-023: Product weight is required.").ifPresent(errors::add);
			ValidatorUtils.validateFieldExists(candidate::getPackageType, "EACT-061: Package type is required.").ifPresent(errors::add);

			validateCubeSize(CandidateUtils.calculateCube(candidate.getInnerLength(), candidate.getInnerWidth(), candidate.getInnerHeight(), 4), MAX_CUBE, "inner cube")
					.ifPresent(errors::add);
			validateCubeSize(CandidateUtils.calculateCube(candidate.getMasterLength(), candidate.getMasterWidth(), candidate.getMasterHeight(), 3), MAX_SHIP_CUBE, "master cube")
					.ifPresent(errors::add);

			errors.addAll(this.validateCostOwnerFields(candidate));
			errors.addAll(this.validateCodeDateDays(candidate));

			errors.addAll(this.validDRU(candidate));

			if (Objects.nonNull(candidate.getSubCommodity())) {
				PhSubCommodity subCommodity = this.phSubCommodityRepository.findByKeySubCommodityCode(candidate.getSubCommodity().getSubCommodityId()).get(0);
				if (Objects.nonNull(candidate.getCandidateProducts()) && !candidate.getCandidateProducts().isEmpty()) {
					candidate.getCandidateProducts().forEach((c) -> errors.addAll(this.validateProduct(candidate.getLane(), subCommodity, c)));
				} else {
					errors.add("EACT-024: Candidate UPCs are required.");
				}
				this.validateItemWeightType(candidate, subCommodity).ifPresent(errors::add);
			} else {
				errors.add("EACT-067: Further errors may be present, but validation cannot continue without sub-commodity.");
			}
		} catch (Exception e) {
			errors.add(String.format("EACT-025: Error while validating candidate: %s", e.getLocalizedMessage()));
		}
		return errors;
	}

	/**
	 * Validates the cost owner and related fields of the candidate.
	 *
	 * @param candidate The candidate to validate.
	 * @return A list of errors. The list will not be null, but can be empty.
	 */
	protected List<String> validateCostOwnerFields(Candidate candidate) {

		List<String> errors = new LinkedList<>();

		if (Objects.isNull(candidate.getBrand()) || BRAND_UNASSIGNED.equals(candidate.getBrand().getBrandId())) {
			errors.add("EACT-026: Brand cannot be set to unassigned.");
		}

		if (Objects.isNull(candidate.getCostOwner())) {
			errors.add("EACT-036: Cost owner cannot be empty.");
		} else {
			Optional<CostOwner> wrappedCostOwner = this.costOwnerRepository.findById(candidate.getCostOwner().getCostOwnerId());
			if (wrappedCostOwner.isEmpty()) {
				errors.add("EACT-037: Cost owner not found.");
			} else {
				CostOwner costOwner = wrappedCostOwner.get();
				if (COST_OWNER_UNASSIGNED.equals(costOwner.getCostOwnerName())) {
					errors.add("EACT-038: Cost owner cannot be set to unassigned.");
				}
				if (!costOwner.getTopToTopId().equals(candidate.getCostOwner().getTopToTopId())) {
					errors.add(String.format("EACT-039: Mismatch between candidate top-to-top of %d and actutal top-to-top of %d.",
							candidate.getCostOwner().getTopToTopId(), costOwner.getTopToTopId()));
				}
				if (TOP_TO_TOP_UNASSIGNED.equals(costOwner.getTopToTopId())) {
					errors.add("EACT-040: Top-to-top cannot be set to unassigned.");
				}

			}
		}

		return errors;
	}

	/**
	 * Validates a CandidateProduct.
	 *
	 * @param lane The lane from the candidate.
	 * @param subCommodity The sub-commodity the candidate will be tied to.
	 * @param candidateProduct The CandidateProduct to validate.
	 * @return A list of errors. The list will not be null, but will be empty if there were no errors.
	 */
	protected List<String> validateProduct(Lane lane, PhSubCommodity subCommodity, CandidateProduct candidateProduct) {
		List<String> errors = new LinkedList<>();

		this.validateField(candidateProduct.getDescription(), "EACT-027", "Product description", true,
				DescriptionType.PRODUCT_DESCRIPTION.getMaxLength()).ifPresent(errors::add);

		this.validateField(candidateProduct.getCustomerFriendlyDescription1(), "EACT-028", "Customer friendly description one", true,
				DescriptionType.CFD_1.getMaxLength()).ifPresent(errors::add);

		this.validateField(candidateProduct.getCustomerFriendlyDescription2(), "EACT-029", "Customer friendly description two", false,
				DescriptionType.CFD_2.getMaxLength()).ifPresent(errors::add);

		if (validationEnabledRomanceCopy) {
			this.validateField(candidateProduct.getRomanceCopy(), "EACT-067", "Romance copy", true, MAX_LENGTH_ROMANCE_COPY).ifPresent(errors::add);
		}

		ValidatorUtils.validateFieldExists(candidateProduct::getUpc, "EACT-030: UPC is required.").ifPresent(errors::add);

		if (Objects.isNull(candidateProduct.getCountryOfOrigin()) || COUNTRY_UNASSIGNED.equals(candidateProduct.getCountryOfOrigin().getCountryId())) {
			errors.add("EACT-031: Country of origin cannot be set to unassigned.");
		}

		if (Objects.isNull(candidateProduct.getWarehouses()) || candidateProduct.getWarehouses().isEmpty()) {
			errors.add("EACT-032: Warehouse assignments are required.");
		} else {
			candidateProduct.getWarehouses().forEach((w) -> errors.addAll(this.validateBicep(lane, subCommodity, w)));
		}

		return errors;
	}

	/**
	 * Validates a description.
	 *
	 * @param description The text to validate.
	 * @param errorCode The error code to return.
	 * @param fieldName The name of the field being validated.
	 * @param required Whether or not the field is required.
	 * @param maxLength The maximum length of the field.
	 * @return An error message. Will be empty if the description passes validation.
	 */
	private Optional<String> validateField(String description, String errorCode, String fieldName, boolean required, int maxLength) {

		if (required && StringUtils.isEmpty(description)) {
			return Optional.of(String.format("%s: %s is required.", errorCode, fieldName));
		}

		if (!required && StringUtils.isEmpty(description)) {
			return Optional.empty();
		}

		if (description.length() > maxLength) {
			return Optional.of(String.format("%s: %s must be between 1 an %d characters.", errorCode, fieldName, maxLength));
		}

		return Optional.empty();
	}

	/**
	 * Validates the code date related fields of a candidate.
	 *
	 * @param candidate The candidate to validate.
	 * @return A list of errors. This will be empty if there are no errors, and will not be null.
	 */
	protected List<String> validateCodeDateDays(Candidate candidate) {

		List<String> errors = new LinkedList<>();

		// If it's not code dated, then no further validation is needed.
		if (!candidate.getCodeDate()) {
			return errors;
		}

		ValidatorUtils.validateFieldExists(candidate::getMaxShelfLife, "EACT-053: Max shelf life is required for code dated items.").ifPresent(errors::add);
		ValidatorUtils.validateFieldExists(candidate::getInboundSpecDays, "EACT-054: Inbound spec days is required for code dated items.").ifPresent(errors::add);
		ValidatorUtils.validateFieldExists(candidate::getWarehouseReactionDays, "EACT-055: Warehouse reaction days is required for code dated items.").ifPresent(errors::add);
		ValidatorUtils.validateFieldExists(candidate::getGuaranteeToStoreDays, "EACT-056: Guarantee to store days is required for code dated items.").ifPresent(errors::add);

		// If any of the fields are missing, dont' continue with validation.
		if (!errors.isEmpty()) {
			return errors;
		}

		// Validate the days themselves.
		this.validateCodeDateField(candidate.getInboundSpecDays() + candidate.getWarehouseReactionDays() + candidate.getGuaranteeToStoreDays(), candidate.getMaxShelfLife(),
				"EACT-057: Inbound spec days + warehouse reaction days + guarantee to store days must less than max shelf life.").ifPresent(errors::add);
		this.validateCodeDateField(candidate.getGuaranteeToStoreDays(), candidate.getWarehouseReactionDays(),
				"EACT-058: Guarantee to store days must be greater than zero and less than warehouse reaction days.").ifPresent(errors::add);
		this.validateCodeDateField(candidate.getWarehouseReactionDays(), candidate.getInboundSpecDays(),
				"EACT-059: Warehouse reaction days must be greater than zero and less than inbound spec days.").ifPresent(errors::add);
		this.validateCodeDateField(candidate.getInboundSpecDays(), candidate.getMaxShelfLife(),
				"EACT-060: Inbound spec days must be greater than zero and less than max shelf life.").ifPresent(errors::add);

		return errors;
	}

	private Optional<String> validateCodeDateField(long fieldValue, long maxFieledValue, String errorMessage) {

		if (fieldValue <= 0 || fieldValue > maxFieledValue) {
			return Optional.of(errorMessage);
		}

		return Optional.empty();
	}

	/**
	 * Validates the vendor/warehouse portion of a candidate.
	 *
	 * @param lane The lane tied to the candidate.
	 * @param subCommodity The sub-commodity we're trying to add the candidate to.
	 * @param warehouse The warehouse the user is trying to put the warehouse into.
	 * @return A list of errors. The list will not be null, but will be empty if there were no errors.
	 */
	protected List<String> validateBicep(Lane lane, PhSubCommodity subCommodity, Warehouse warehouse) {

		if (Objects.isNull(lane) || Objects.isNull(subCommodity)) {
			return List.of("EACT-033: Lane and Sub-commodity are required.");
		}

		// Check that the vendor/warehouse combination is valid.
		Long bicep = CandidateUtils.bicepFromLaneAndWarehouse(lane, warehouse);
		LocationKey locationKey = new LocationKey();
		locationKey.setLocationNumber(bicep).setLocationType(LocationKey.LOCATION_TYPE_VENDOR);
		Optional<VendorLocation> vendorLocation = this.vendorLocationRepository.findById(locationKey);
		if (vendorLocation.isEmpty()) {
			return List.of(String.format("EACT-034: Bicep %d is not valid.", bicep));
		}
		VendorLocation vl = vendorLocation.get();
		if (!vl.getMixedItemClassSwitch().equals(CandidateUtils.YES) && !Long.valueOf(vl.getClassCode().trim()).equals(subCommodity.getCommodity().getItemClass().getItemClassCode())) {
			return List.of(String.format("EACT-035: Bicep %d is not tied to class for sub-commodity %d.", bicep, subCommodity.getKey().getSubCommodityCode()));
		}
		return List.of();
	}

	/**
	 * Validates the cost components of the candidate.
	 *
	 * @param candidate The candidate to validate.
	 * @return An optional error message. May be empty.
	 */
	protected Optional<String> validateCost(Candidate candidate) {

		if (Objects.nonNull(candidate.getCostLinkBy())) {
			return ValidatorUtils.validateFieldExists(candidate::getCostLinkFromServer, "EACT-041: Candidate is configured to be cost link, but cost link is not set.");
		} else {
			return ValidatorUtils.validateFieldExists(candidate::getInnerListCost, "EACT-042: Cost is required.");
		}
	}

	/**
	 * Validates retail size.
	 *
	 * @param candidate the candidate to validate.
	 * @return Optional error message.
	 */
	protected Optional<String> validateRetailSize(Candidate candidate) {

		if (!StringUtils.hasText(candidate.getRetailSize())) {
			return Optional.of("EACT-006: Retail size is required.");
		} else {
			return candidate.getRetailSize().length() > MAX_RETAIL_SIZE ?
					Optional.of(String.format("EACT-003: Retail Size cannot be greater than %d characters.", MAX_RETAIL_SIZE)) :
					Optional.empty();
		}
	}

	/**
	 * Validates the item weight type.
	 * @param candidate the candidate to validate on.
	 * @param subCommodity the commodity to compare against.
	 * @return Optional error message.
	 */
	protected Optional<String> validateItemWeightType(Candidate candidate, PhSubCommodity subCommodity) {

		String deptId = String.format(ITEM_CLASS_DEPARTMENT_AS_STRING_CONVERSION,
				subCommodity.getCommodity().getItemClass().getDepartmentId());
		if (Objects.nonNull(candidate.getCommodity().getDepartmentId()) && !candidate.getCommodity().getDepartmentId().equalsIgnoreCase(deptId)) {
			return Optional.of(String.format("EACT-063: Mismatch between candidate commodity department of %s and actual commodity department of %s",
					deptId, candidate.getCommodity().getDepartmentId()));
		}
		if (this.isItemWeightTypeDepartment(deptId)) {
			if (StringUtils.isEmpty(candidate.getItemWeightType().trim())) {
				return Optional.of("EACT-064: Candidate is missing an Item Weight Type (None, Catch Weight, Variable Weight).");
			} else if (!this.isValidItemWeightType(candidate.getItemWeightType().trim())) {
				return Optional.of(String.format("EACT-065: Candidate Item Weight Type: '%s' is not a valid type (None, Catch Weight, Variable Weight).", candidate.getItemWeightType()));
			}
		} else {
			if (!StringUtils.isEmpty(candidate.getItemWeightType())) {
				return Optional.of(String.format("EACT-066: Candidate contains Item Weight Type: '%s' even though it is not in an item weight department.", candidate.getItemWeightType()));
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns whether or not the commodity contains a department that could require catch or variable weight.
	 * @param deptId the departmentId.
	 *
	 * @return whether or not the commodity contains a department that could require catch or variable weight.
	 */
	private boolean isItemWeightTypeDepartment(String deptId) {
		return ITEM_WEIGHT_DEPT_LIST.stream().anyMatch(deptId::equalsIgnoreCase);
	}

	/**
	 * Returns whether or not the item weight type is valid.
	 *
	 * @param itemWeight the item weight type.
	 * @return whether or not the item weight type is valid.
	 */
	private boolean isValidItemWeightType(String itemWeight) {
		return itemWeight.equalsIgnoreCase(CandidateUtils.NONE_ITEM_WEIGHT_TYPE) ||
				itemWeight.equalsIgnoreCase(CandidateUtils.VARIABLE_WEIGHT_ITEM_WEIGHT_TYPE) ||
				itemWeight.equalsIgnoreCase(CandidateUtils.CATCH_WEIGHT_ITEM_WEIGHT_TYPE);
	}
	/**
	 * Validates the commodity, sub-commodity, and BDM in the candidate.
	 *
	 * @param candidate The candidate to check.
	 * @return An error message if the commodity, sub-commodity, or BDM does not validate and empty otherwise.
	 */
	protected Optional<String> validateCommodityAndSubCommodity(Candidate candidate) {

		if (Objects.isNull(candidate.getCommodity()) || Objects.isNull(candidate.getSubCommodity())) {
			return Optional.of("EACT-043: Commodity and sub-commodity are required.");
		}

		// Pull the sub-commodity supplied by the user.
		List<PhSubCommodity> subCommodities = this.phSubCommodityRepository.findByKeySubCommodityCode(candidate.getSubCommodity().getSubCommodityId());
		if (subCommodities.isEmpty() || subCommodities.size() > 1) {
			return Optional.of(String.format("EACT-044: Sub-commodity %d is not valid.", candidate.getCommodity().getCommodityId()));
		}

		// Make sure the commodity from the candidate matches the one we pull.
		PhSubCommodity subCommodity = subCommodities.get(0);
		if (Objects.isNull(subCommodity.getCommodity())) {
			return Optional.of(String.format("EACT-045: Sub-commodity %s is in an error state.", subCommodity.getKey().getSubCommodityCode()));
		}

		// Make sure the commodity in the candidate matches the commodity tied to the candidate's sub-commodity.
		if (!subCommodity.getCommodity().getKey().getCommodityCode().equals(candidate.getCommodity().getCommodityId())) {
			return Optional.of(String.format("EACT-046: Mismatch between candidate commodity of %d and actual commodity of %d",
					candidate.getCommodity().getCommodityId(),
					subCommodity.getCommodity().getKey().getCommodityCode()));
		}


		if (Objects.isNull(candidate.getBuyer())) {
			return Optional.of("EACT-047: BDM is not set in the candidate.");
		}

		if (!subCommodity.getCommodity().getBdm().getBdmCode().equals(candidate.getBuyer().getBuyerId())) {
			return Optional.of("EACT-048: Mismatch between candidate buyer and buyer tied to the candidate's commodity.");
		}

		return ValidatorUtils.validateStringFieldHasValue(() -> CandidateUtils.vertexTaxCategoryFromTaxableAndSubCommodity(candidate.getTaxable(), subCommodity),
				"EACT-062: Vertex tax category is required.");
	}

	/**
	 * Validates the retail fields are set correctly.
	 *
	 * @param candidate The candidate to check.
	 * @return An error message if validation fails and empty otherwise.
	 */
	protected Optional<String> validateRetail(Candidate candidate) {

		if (Objects.isNull(candidate.getRetailType())) {
			return Optional.of("EACT-049: Retail type is not defined.");
		}

		Optional<String> toReturn = Optional.empty();

		// Make sure the retail is set appropriately.
		switch (candidate.getRetailType()) {
			case Candidate.RETAIL_LINK:
				toReturn = this.validateRetailLink(candidate);
				break;
			case Candidate.KEY_RETAIL:
				toReturn = this.validateKeyedRetail(candidate);
				break;
			case Candidate.PRICE_REQUIRED:
				break;
			default:
				return Optional.of(String.format("EACT-050: Unknown retail type %s", candidate.getRetailType()));
		}

		return toReturn;
	}

	/**
	 * Validates the retail link of the candidate. This method assumes the user chose retail linking.
	 *
	 * @param candidate The candidate to validate.
	 * @return An optional error message. This may be empty.
	 */
	protected Optional<String> validateRetailLink(Candidate candidate) {
		if (Objects.isNull(candidate.getRetailLink()) || Objects.isNull(candidate.getRetailLink().getUnitUpc())) {
			return Optional.of("EACT-051: Candidate is configured to be in a retail link, but the link is not set.");
		}
		return Optional.empty();
	}

	/**
	 * Validates the keyed retail of a candidate. This method assumes the user chose to key in a retial.
	 *
	 * @param candidate The candidate to validate.
	 * @return An optional error message. This may be empty.
	 */
	protected Optional<String> validateKeyedRetail(Candidate candidate) {
		if (Objects.isNull(candidate.getRetailXFor()) || Objects.isNull(candidate.getRetailPrice())) {
			return Optional.of("EACT-052: Candidate is configured with a set retail, but the retail is not provided.");
		}
		if (candidate.getRetailXFor().compareTo(MINIMUM_REQUIRED_XFOR) < 0) {
			return Optional.of("EACT-002: Retail must be greater than zero.");
		}
		if (candidate.getRetailPrice().compareTo(MINIMUM_REQUIRED_RETAIL) <= 0) {
			return Optional.of("EACT-002: Retail must be greater than zero.");
		}
		return Optional.empty();
	}

	/**
	 * Validates a calculated cube will fit in the field it's meant for.
	 *
	 * @param cube The calculated cube.
	 * @param maxCube The max for the cube.
	 * @param type the type of cube being calculated. This will be added to the error message.
	 * @return An error message if the calculated cube is too big and empty if not.
	 */
	protected Optional<String> validateCubeSize(BigDecimal cube, BigDecimal maxCube, String type) {

		if (maxCube.compareTo(cube) < 0) {
			return Optional.of(String.format("EACT-077: Calculation of %s results in a value that cannot be stored.", type));
		}
		return Optional.empty();
	}

	/**
	 * Validate the DRU fields.
	 *
	 * @param candidate to validate DRU fields.
	 * @return list of errors.
	 */
	public List<String> validDRU(Candidate candidate) {
		return CandidateDRUValidator.validDRU(candidate);
	}
}
