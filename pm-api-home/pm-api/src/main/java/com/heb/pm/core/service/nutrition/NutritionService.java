package com.heb.pm.core.service.nutrition;

import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.core.model.updates.nutrition.NutrientStatement;
import com.heb.pm.dao.core.entity.ProductMaster;
import com.heb.pm.core.model.Product;
import com.heb.pm.dao.core.entity.codes.PanelType;
import com.heb.pm.util.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


/**
 * Service to handle pulling nutrition data.
 *
 * @author d116773
 * @since 1.4.0
 */
@Service
public class NutritionService {

	@Autowired
	private transient EcommerceAgPanelService agPanelService;

	@Autowired
	private transient EcommerceFdaPanelService fdaPanelService;

	@Autowired
	private transient ScaleFdaPanelService scaleFdaPanelService;

	@Autowired
	private transient ScaleAgPanelService scaleAgPanelService;

	@Autowired
	private transient EcommerceSupplimentPanel ecommerceSupplimentPanel;

	@Autowired
	private transient Nutrition1990PersistenceService nutrition1990Service;

	@Autowired
	private transient Nutrition2016PersistenceService nutrition2016Service;

	/**
	 * Populates nutrition panels for a product if they exist.
	 *
	 * @param product The Product to add nutrition data to. This object may be modified.
	 * @param productMaster The ProductMaster for the product to get the nutrition for.
	 */
	public void populateNutrition(Product product, ProductMaster productMaster) {

		// First, try and see if this is a scale item with a NLEA2016 panel.
		if (this.scaleFdaPanelService.populateScaleNutrition(product, productMaster)) {
			return;
		}

		// If not, see if it's a NLEA1990 scale item.
		if (this.scaleAgPanelService.populateScaleNutrition(product, productMaster)) {
			return;
		}

		// Next, see if this one has an NLEA2016 panel in the eCommerce data.
		if (this.fdaPanelService.populatePublishedEcommerceNutritionPanels(product, productMaster)) {
			return;
		}

		// Next, see if there is an NLEA1990 panel in the eCommerce data.
		if (this.agPanelService.populatePublishedEcommerceNutritionPanels(product, productMaster)) {
			return;
		}

		// Finally, see if it's a supplement panel.
		this.ecommerceSupplimentPanel.populatePublishedEcommerceNutritionPanels(product, productMaster);
	}

	/**
	 * Saves a NutrientStatement.
	 *
	 * @param upc The UPC the nutrient statement is for.
	 * @param nutrientStatement The NutrientStatement to save.
	 * @return A response with the ID of the statement, and, possibly, the UPC it is meant for and if it is tied to the UPC.
	 */
	public NutritionUpdateResponse saveNutrientStatement(Long upc, NutrientStatement nutrientStatement) {

		// Do some basic checks on the passed in statement.
		List<String> errors = new LinkedList<>();

		ValidatorUtils.validateStringFieldHasValue(nutrientStatement::getPanelType, "Panel type is required.").ifPresent(errors::add);
		ValidatorUtils.validateFieldInList(nutrientStatement::getPanelType, "Panel type must be 'N1990' or 'N2016'", "N1990", "N2016").ifPresent(errors::add);

		// If there are errors already, we can't do further validations, so just throw this error.
		if (!errors.isEmpty()) {
			throw new ValidationException("Unable to validate scale nutrient statement.", errors);
		}

		// Delegate saving the statement to a service specific to the panel type.
		PanelType requestedPanelType = PanelType.of(nutrientStatement.getPanelType());
		if (PanelType.NLEA_1990.equals(requestedPanelType)) {
			return this.nutrition1990Service.save(upc, nutrientStatement);
		} else {
			return this.nutrition2016Service.save(upc, nutrientStatement);
		}
	}
}
