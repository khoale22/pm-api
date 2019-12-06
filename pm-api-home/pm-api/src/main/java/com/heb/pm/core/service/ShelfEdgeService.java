package com.heb.pm.core.service;

import com.heb.pm.core.model.Product;
import com.heb.pm.dao.core.entity.ProductDescription;
import com.heb.pm.dao.core.entity.ProductMaster;
import org.springframework.stereotype.Service;

/**
 * Holds business logic related to adding shelf-edge related fields to a product.
 *
 * @author d116773
 * @since 1.9.0
 */
@Service
public class ShelfEdgeService {

	private static final String SHOW_CALORIES_ON_SHELF_TAG = "Y";

	/**
	 * Driver method that will add all the shelf-edge related attributes to a product.
	 *
	 * @param product The Product to add the attributes to. This object may be modified.
	 * @param productMaster The ProductMaster to pull the attribtues from.
	 */
	public void applyShelfEdgeAttributes(Product product, ProductMaster productMaster) {

		// Delegate adding the various descriptions
		this.applyProductDescriptions(product, productMaster);

		// Should the shelf-tag show the number of calories and serviing size for this product.
		product.setShowCalories(SHOW_CALORIES_ON_SHELF_TAG.equals(productMaster.getShowCalories()));
	}

	/**
	 * Adds the various product descriptions to a product object.
	 *
	 * @param product The Product to add the descriptions to. This object will be modified.
	 * @param productMaster The ProductMaster to pull the descriptions from.
	 */
	private void applyProductDescriptions(Product product, ProductMaster productMaster) {


		for (ProductDescription productDescription : productMaster.getProductDescriptions()) {

			switch (productDescription.getKey().getDescriptionType()) {

				case SIGN_ROMANCE:
					product.setSignRomanceCopy(productDescription.getDescription());
					break;
				case SERVICE_CASE_CALLOUT:
					product.setServiceCaseCallout(productDescription.getDescription());
					break;
				case POS:
					product.setReceiptDescription(productDescription.getDescription());
					break;
				case CFD_1:
					product.setCustomerFriendlyDescriptionOne(productDescription.getDescription());
					break;
				case CFD_2:
					product.setCustomerFriendlyDescriptionTwo(productDescription.getDescription());
					break;
				case PRIMO_PICK:
					product.setPrimoPickDescription(productDescription.getDescription());
					break;
				default:
						// Do Nothing
			}
		}
	}
}
