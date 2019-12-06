package com.heb.pm.core.service.validators;

import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import com.heb.pm.util.UpcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Validates the data a vendor enters into a candidate.
 *
 * @author d116773
 * @since 1.0.0
 */
@SuppressWarnings("PMD.GodClass")
@Component
/* default */ class VendorDataValidator implements CandidateValidator {

    @Value("${validation.enabled.romanceCopy}")
    private transient boolean validationEnabledRomanceCopy;

    public static final int MAX_RETAIL_SIZE = 6;

    // DRU constants
    public static final int MAX_DRU_ROWS = 9_999;
    public static final String ORIENTATION_BY_DEPTH = "stock_facing_by_depth";
    public static final String ORIENTATION_BY_DEPTH_OR_FACING = "stock_facing_by_depth_or_facing";
    public static final String DRU_TYPE_RRP = "retail_ready_packaging";
    public static final String DRU_TYPE_DRP = "display_ready_pallets";
    public static final int MAX_ROMANCE_COPY_LENGTH = 4_000;

    /**
     * Validates the vendor entered data of a candidate.
     *
     * @param candidate The candidate to validate.
     * @return The validated candidate.
     */
    @Override
    public void validate(Candidate candidate) throws ValidationException {

        List<String> errors = new LinkedList<>();

        this.validRequiredVendorFields(candidate).ifPresent(errors::add);
        this.validRetailSize(candidate).ifPresent(errors::add);
        this.validDimensions(candidate).ifPresent(errors::add);

        errors.addAll(this.validateProducts(candidate.getCandidateProducts()));
        errors.addAll(this.validateCostLink(candidate));
        errors.addAll(this.validDRU(candidate));

        if (!errors.isEmpty()) {
            throw new ValidationException("Unable to validate vendor data.", errors);
        }
    }

    /**
     * Validate if all the required fields are completed.
     *
     * @param candidate candidate.
     * @return List of error(s)
     */
    protected Optional<String> validRequiredVendorFields(Candidate candidate) {

        if (candidate.getVendor() == null || candidate.getBuyer() == null || candidate.getBrand() == null || candidate.getCostOwner() == null
                || candidate.getCostOwner().getTopToTopId() == null || candidate.getTotalVolume() == null || candidate.getProductLength() == null
                || candidate.getUnitOfMeasure() == null || candidate.getProductWidth() == null || candidate.getRetailSize() == null
                || candidate.getProductHeight() == null || candidate.getPackageType() == null || candidate.getProductWeight() == null
                || candidate.getVendorTier() == null || candidate.getInnerListCost() == null || candidate.getVendorTie() == null
                || candidate.getMasterWidth() == null || candidate.getInnerWidth() == null || candidate.getMasterWeight() == null
                || candidate.getMasterListCost() == null
                || candidate.getInnerWeight() == null || candidate.getMaxShelfLife() == null && candidate.getCodeDate() != null
                && candidate.getCodeDate() || StringUtils.isBlank(candidate.getProductType())
                || !this.validateChannelAndLane(candidate)) {
            return Optional.of("Please enter the required fields.");
        }
        return Optional.empty();
    }

    /**
     * Validate if the inner pack dimensions are less than master pack dimensions.
     *
     * @param candidate candidate
     * @return error(s) regarding dimensions
     */
    protected Optional<String> validDimensions(Candidate candidate) {

        if (candidate.getInnerHeight() == null || candidate.getMasterHeight() == null || candidate.getInnerLength() == null || candidate.getMasterLength() == null
                || candidate.getInnerPack() == null || candidate.getMasterPack() == null || candidate.getInnerWeight() == null || candidate.getMasterWeight() == null
                || candidate.getInnerWidth() == null || candidate.getMasterWidth() == null) {
            return Optional.of("Please enter the required dimensions fields");
        }
        if (candidate.getInnerHeight().compareTo(candidate.getMasterHeight()) > 0 || candidate.getInnerLength().compareTo(candidate.getMasterLength()) > 0
                || candidate.getInnerPack().compareTo(candidate.getMasterPack()) > 0 || candidate.getInnerWeight().compareTo(candidate.getMasterWeight()) > 0
                || candidate.getInnerWidth().compareTo(candidate.getMasterWidth()) > 0) {
            return Optional.of("Inner dimensions cannot be larger than master dimensions");
        }
        return Optional.empty();
    }

    /**
     * Validates upcs and required field for for all candidate products.
     *
     * @param products candidate products
     * @return List of errors.
     */
    protected List<String> validateProducts(List<CandidateProduct> products) {

        List<String> errorMessages = new LinkedList<>();

        if (Objects.isNull(products) || products.isEmpty()) {
            errorMessages.add("At least one product must be added.");
            return errorMessages;
        }

        for (int index = 0; index < products.size(); index++) {
            CandidateProduct product = products.get(index);

            this.validateProductHasRequiredFields(product, index).ifPresent(errorMessages::add);
            try {
                // This can throw an exception if the UPC is not in the correct format.
                this.validateCheckDigit(product.getUpc(), product.getUpcCheckDigit()).ifPresent(errorMessages::add);
                this.validateCheckDigit(product.getCaseUpc(), product.getCaseUpcCheckDigit()).ifPresent(errorMessages::add);
            } catch (IllegalArgumentException e) {
                errorMessages.add(e.getLocalizedMessage());
            }
        }

        return errorMessages;
    }

    /**
     * Validates a product record to ensure all required fields are present.
     *
     * @param product The product to look at.
     * @param index   The index of the product in the list.
     * @return An optional error message.
     */
    protected Optional<String> validateProductHasRequiredFields(CandidateProduct product, int index) {
        if (product.getCaseUpc() == null || product.getUpc() == null || product.getCaseUpcCheckDigit() == null ||
                product.getUpcCheckDigit() == null || StringUtils.isBlank(product.getDescription())
                || StringUtils.isBlank(product.getVendorProductCode())
                || StringUtils.isBlank(product.getCustomerFriendlyDescription1())) {

            return Optional.of(String.format("Candidate product has missing required information in row %d", index));
        }

        if (validationEnabledRomanceCopy && (product.getRomanceCopy() == null || StringUtils.isBlank(product.getRomanceCopy()) ||
                product.getRomanceCopy().length() > MAX_ROMANCE_COPY_LENGTH)) {
            return Optional.of(String.format(
                    "Candidate product romance copy is not specified or exceeds maximum length in row %d", index));
        }

        return Optional.empty();
    }

    /**
     * Whether there is a valid channel to lane relationship.
     */
    protected boolean validateChannelAndLane(Candidate toValidate) {
        // if candidate is warehouse
        if (Boolean.TRUE.equals(toValidate.getWarehouseSwitch())) {
            // if lane is not null, return true, else return false
            return !Objects.isNull(toValidate.getLane());
            // else return false; this will have to be reworked once we add DSD channel
        } else {
            // TODO: Set code for both, or just dsd when the business gives us rules.
            return true;
        }
    }

    /**
     * Validates a check digit against a UPC. This method assumes a validation check
     * that both are present has already been done elsewhere and will not return an error
     * in that case.
     *
     * @param upc        The UPC to check.
     * @param checkDigit The check digit to compare against.
     * @return An optional error message.
     */
    protected Optional<String> validateCheckDigit(Long upc, Long checkDigit) {

        if (upc == null || checkDigit == null) {
            return Optional.empty();
        }

        if (UpcUtils.validateCheckDigit(upc, checkDigit.intValue())) {
            return Optional.empty();
        }

        return Optional.of(String.format("Check digit %d is not valid for UPC %d", checkDigit, upc));
    }


    /**
     * Checks to make sure the vendor data matches the cost link if it is present.
     *
     * @param candidate The candidate to validate.
     * @return A list of error messages if there is an issue and an empty otherwise.
     */
    protected List<String> validateCostLink(Candidate candidate) {

        if (Objects.isNull(candidate.getCostLinkFromServer())) {
            return List.of();
        }

        List<String> errorMessages = new LinkedList<>();


        if (Objects.isNull(candidate.getVendor()) ||
                !candidate.getCostLinkFromServer().getVendor().getApNumber().equals(candidate.getVendor().getApNumber())) {
            errorMessages.add("Cost link vendor does not match candidate vendor.");
        }
        if (candidate.getCostLinkFromServer().getPack().intValue() != candidate.getInnerPack()) {
            errorMessages.add("Cost link pack does not match candidate pack.");
        }
        return errorMessages;
    }

    /**
     * Returns error string if retail size is invalid.
     *
     * @param candidate the candidate to validate.
     * @return An error string if retail size is invalid.
     */
    protected Optional<String> validRetailSize(Candidate candidate) {
        if (StringUtils.isNotEmpty(candidate.getRetailSize()) && candidate.getRetailSize().length() > MAX_RETAIL_SIZE) {
            return Optional.of(String.format("Length of retail size: '%s' is greater than 6 characters.", candidate.getRetailSize()));
        }
        return Optional.empty();
    }

    /**
     * Returns error string if DRU fields are invalid.
     *
     * @param candidate the candidate to validate.
     * @return An error string if DRU fields are invalid.
     */
    protected List<String> validDRU(Candidate candidate) {

        List<String> errorMessages = new LinkedList<>();

        if (Objects.nonNull(candidate.getDisplayReadyUnit())) {
            if (Objects.nonNull(candidate.getDisplayReadyUnitRowsDeep()) && (candidate.getDisplayReadyUnitRowsDeep() >
                    MAX_DRU_ROWS || candidate.getDisplayReadyUnitRowsDeep() <= 0)) {
                errorMessages.add("Length of Rows Deep in Retail Units is not between 1 and " + MAX_DRU_ROWS + ".");
            } else if (Objects.isNull(candidate.getDisplayReadyUnitRowsDeep())) {
                errorMessages.add("Rows Deep in Retail Units is not specified.");
            }

            if (Objects.nonNull(candidate.getDisplayReadyUnitRowsFacing()) && (candidate.getDisplayReadyUnitRowsFacing() >
                    MAX_DRU_ROWS || candidate.getDisplayReadyUnitRowsFacing() <= 0)) {
                errorMessages.add("Length of Rows Facing in Retail Units is not between 1 and " + MAX_DRU_ROWS + ".");
            } else if (Objects.isNull(candidate.getDisplayReadyUnitRowsFacing())) {
                errorMessages.add("Rows Facing in Retail Units is not specified.");
            }

            if (Objects.nonNull(candidate.getDisplayReadyUnitRowsHigh()) && (candidate.getDisplayReadyUnitRowsHigh() >
                    MAX_DRU_ROWS || candidate.getDisplayReadyUnitRowsHigh() <= 0)) {
                errorMessages.add("Length of Rows High in Retail Units is not between 1 and " + MAX_DRU_ROWS + ".");
            } else if (Objects.isNull(candidate.getDisplayReadyUnitRowsHigh())) {
                errorMessages.add("Rows High in Retail Units is not specified.");
            }

            if (Objects.nonNull(candidate.getDisplayReadyUnitOrientation()) &&
                    !candidate.getDisplayReadyUnitOrientation().equals(ORIENTATION_BY_DEPTH) &&
                    !candidate.getDisplayReadyUnitOrientation().equals(ORIENTATION_BY_DEPTH_OR_FACING)) {
                errorMessages.add("DRU Orientation is not specified correctly.");
            } else if (Objects.isNull(candidate.getDisplayReadyUnitOrientation())) {
                errorMessages.add("DRU Orientation is not specified.");
            }

            if (Objects.nonNull(candidate.getDisplayReadyUnitType()) &&
                    !candidate.getDisplayReadyUnitType().equals(DRU_TYPE_DRP) &&
                    !candidate.getDisplayReadyUnitType().equals(DRU_TYPE_RRP)) {
                errorMessages.add("DRU type is not specified correctly.");
            } else if (Objects.isNull(candidate.getDisplayReadyUnitType())) {
                errorMessages.add("DRU type is not specified.");
            }

            if (Objects.nonNull(candidate.getDisplayReadyUnitRowsDeep()) &&
                    Objects.nonNull(candidate.getDisplayReadyUnitRowsFacing()) &&
                    Objects.nonNull(candidate.getDisplayReadyUnitRowsHigh()) &&
                    Objects.nonNull(candidate.getInnerPack()) &&
                    !this.calculateDRUDimensions(candidate).equals(candidate.getInnerPack())) {
                errorMessages.add("The Rows Facing x Rows Deep x Rows High must be equal to the Inner Pack. " +
                        "Please update the values.");
            }
        }

        return errorMessages;
    }

    /**
     * Calculate the DRU Dimensions.
     *
     * @param candidate to check.
     * @return dimension calculation.
     */
    private Long calculateDRUDimensions(Candidate candidate) {
        return candidate.getDisplayReadyUnitRowsDeep() *
                candidate.getDisplayReadyUnitRowsFacing() *
                candidate.getDisplayReadyUnitRowsHigh();
    }

    @Override
    public boolean handles(CandidateValidatorType validatorType) {
        return CandidateValidatorType.VENDOR_DATA_VALIDATOR.equals(validatorType);
    }
}
