package com.heb.pm.core.service.validators;

import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.Commodity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Validates the data supplier by a buyer.
 *
 * @author d116773
 * @since 1.0.0
 */
@Component
/* default */ class BuyerDataValidator implements CandidateValidator {

	private static final Logger logger = LoggerFactory.getLogger(BuyerDataValidator.class);

	private static final String NONE_ITEM_WEIGHT_TYPE = "None";
	private static final String CATCH_WEIGHT_ITEM_WEIGHT_TYPE = "Catch Weight";
	private static final String VARIABLE_WEIGHT_ITEM_WEIGHT_TYPE = "Variable Weight";
	private static final List<String> ITEM_WEIGHT_DEPT_LIST = new ArrayList<>(Arrays.asList("02", "06", "09"));

	/**
	 * Validates a candidate at the buyer entry step of the workflow.
	 *
	 * @param candidate The candidate to validate.
	 * @return The validated candidate.
	 */
	@Override
	public void validate(Candidate candidate) throws ValidationException {

		List<String> errorMessages = new LinkedList<>();

		this.validateRequiredBuyerFields(candidate).ifPresent(errorMessages::add);
		this.validateCodeDateDays(candidate).ifPresent(errorMessages::add);
		this.validateRetailFields(candidate).ifPresent(errorMessages::add);
		this.validateItemWeightType(candidate).ifPresent(errorMessages::add);

		this.validateCostLink(candidate);

		if (!errorMessages.isEmpty()) {
			throw new ValidationException("Unable to validate buyer data.", errorMessages);
		}
	}

	@Override
	public boolean handles(CandidateValidatorType validatorName) {
		return CandidateValidatorType.BUYER_DATA_VALIDATOR.equals(validatorName);
	}

	/**
	 * Validates that the fields of the buyer stage has all the required values.
	 *
	 * @param candidate The candidate to review.
	 * @return An optional error message.
	 */
	protected Optional<String> validateRequiredBuyerFields(Candidate candidate) {

		boolean valid = Objects.nonNull(candidate.getCommodity()) && Objects.nonNull(candidate.getCommodity().getCommodityId());
		valid &= Objects.nonNull(candidate.getSubCommodity()) && Objects.nonNull(candidate.getSubCommodity().getSubCommodityId());
		valid &= Objects.nonNull(candidate.getRetailType());
		valid &= Objects.nonNull(candidate.getRetailXFor());
		valid &= Objects.nonNull(candidate.getRetailPrice());
		if (candidate.getCodeDate() != null && candidate.getCodeDate()) {
			valid &= Objects.nonNull(candidate.getInboundSpecDays());
			valid &= Objects.nonNull(candidate.getWarehouseReactionDays());
			valid &= Objects.nonNull(candidate.getGuaranteeToStoreDays());
		}

		if (valid) {
			return Optional.empty();
		}
		return Optional.of("Please enter the required fields.");
	}

	/**
	 * Validates the code date values of a candidate.
	 *
	 * @param candidate The candidate to review.
	 * @return An optional error message.
	 */
	protected Optional<String> validateCodeDateDays(Candidate candidate) {
		if (Objects.isNull(candidate.getCodeDate()) || !candidate.getCodeDate()) {
			return Optional.empty();
		}

		boolean valid = candidate.getInboundSpecDays() < candidate.getMaxShelfLife();
		valid &= candidate.getWarehouseReactionDays() < candidate.getInboundSpecDays();
		valid &= candidate.getGuaranteeToStoreDays() < candidate.getWarehouseReactionDays();

		if (!valid) {
			return Optional.of("Guarantee to Store days must be less than Reaction Days.\nReaction Days must be less than Inbound Spec Days.\n" +
					"Inbound Spec Days must be less than Max Shelf Life.");
		} else if ((candidate.getInboundSpecDays() + candidate.getWarehouseReactionDays() +
				candidate.getGuaranteeToStoreDays()) > candidate.getMaxShelfLife()) {
			return Optional.of("Max Shelf Life must be greater than the sum of Inbound Spec Days, Reaction Days, and Guarantee to Store Days.");

		}
		return Optional.empty();
	}

	/**
	 * Makes sure the cost link and commodity information match. Warning, if they do not, the cost link information
	 * is removed.
	 *
	 * @param candidate the candidate to validate.
	 */
	protected void validateCostLink(Candidate candidate) {

		if (Objects.isNull(candidate.getCostLinkFromServer())) {
			return;
		}

		// If the sub-commodity doesn't match the cost link, remove the cost link information.
		// See PAM-854.
		if (!candidate.getCommodity().getCommodityId().equals(
				Long.valueOf(candidate.getCostLinkFromServer().getCommodity()))) {
			logger.warn(String.format("Removing cost link information for candidate %d", candidate.getCandidateId()));
			candidate.setCostLinkFromServer(null);
			candidate.setCostLink(null);
			candidate.setCostLinkBy(null);
		}
	}

	/**
	 * Validates the retail fields of a candidate.
	 *
	 * @param candidate The candidate to review.
	 * @return An optional error message.
	 */
	protected Optional<String> validateRetailFields(Candidate candidate) {

		if (Objects.isNull(candidate.getProductType())) {
			return Optional.of("Product type is required.");
		}

		if (candidate.getProductType().equals(Candidate.NON_SELLABLE)) {
			if (candidate.getRetailXFor() != 1 || candidate.getRetailPrice().compareTo(BigDecimal.ZERO) != 0) {
				return Optional.of("Retail must be 1 for $0.00 for non-sellable product.");
			}
		} else {
			if (Objects.isNull(candidate.getRetailType())) {
				return Optional.of("Retail type must be set.");
			}
			switch (candidate.getRetailType()) {
				case Candidate.PRICE_REQUIRED:
					if (candidate.getRetailXFor() != 1 || candidate.getRetailPrice().compareTo(BigDecimal.ZERO) != 0) {
						return Optional.of("Retail must be 1 for $0.00 for price required product.");
					}
					break;
				case Candidate.KEY_RETAIL:
					if (candidate.getRetailXFor() < 1 || candidate.getRetailPrice().compareTo(BigDecimal.ZERO) <= 0) {
						return Optional.of("Retail xFor must be 1 or more and Retail Price must be greater than 0 for keyed retails.");
					}
					break;
				case Candidate.RETAIL_LINK:
					if (Objects.isNull(candidate.getRetailLink()) || Objects.isNull(candidate.getRetailLink().getRetailLinkNumber())) {
						return Optional.of("Retail link is required when the retail link option is selected.");
					}
					break;

				default:
					return Optional.of("Unknown retail type.");
			}
		}
		return Optional.empty();
	}

	/**
	 * Validates Item Weight type.
	 * @param candidate the candidate to validate
	 * @return optional error.
	 */
	protected Optional<String> validateItemWeightType(Candidate candidate) {
		if (Objects.isNull(candidate.getCommodity())) {
			return Optional.of("Commodity is required");
		}
		if (this.isItemWeightTypeDepartment(candidate.getCommodity())) {
			if (StringUtils.isBlank(candidate.getItemWeightType())) {
				return Optional.of("Item weight type is required.");
			} else {
				if (!(candidate.getItemWeightType().equalsIgnoreCase(NONE_ITEM_WEIGHT_TYPE) ||
						candidate.getItemWeightType().equalsIgnoreCase(VARIABLE_WEIGHT_ITEM_WEIGHT_TYPE) ||
						candidate.getItemWeightType().equalsIgnoreCase(CATCH_WEIGHT_ITEM_WEIGHT_TYPE))) {
					return Optional.of(String.format("Item weight type '%s' is not valid.",
							candidate.getItemWeightType()));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns whether or not the commodity contains a department that could require catch or variable weight.
	 * @param commodity the commodity
	 *
	 * @return whether or not the commodity contains a department that could require catch or variable weight.
	 */
	private boolean isItemWeightTypeDepartment(Commodity commodity) {
		return ITEM_WEIGHT_DEPT_LIST.stream().anyMatch(commodity.getDepartmentId().trim()::equalsIgnoreCase);
	}
}
