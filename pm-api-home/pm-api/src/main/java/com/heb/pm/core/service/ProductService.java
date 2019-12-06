package com.heb.pm.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.pm.core.endpoint.SearchCriteria;
import com.heb.pm.dao.core.entity.ProdItem;
import com.heb.pm.dao.core.entity.ProductMaster;
import com.heb.pm.core.model.Item;
import com.heb.pm.core.model.Product;
import com.heb.pm.core.repository.ProductRepository;
import com.heb.pm.core.repository.UserSearchRepository;
import com.heb.pm.core.service.nutrition.NutritionService;
import com.heb.pm.util.ListUtils;
import com.heb.pm.util.json.StreamingOutputWriter;
import com.heb.pm.util.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This holds all of the business logic for Products.
 *
 * @author s573181
 * @since 1.0.0
 */
@Service
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	private static final String NAME_AND_CODE_FORMAT = "%s [%s]";
	private static final String UNKNOWN_USER_NAME = "UNKNOWN";
	private static final int MAX_USERS_PER_ID = 1;

	public static final String SUPPLY_CHAIN_FILTER = "SUPPLY-CHAIN";
	public static final String ECOMMERCE_FILTER = "ECOMMERCE";
	public static final String NUTRITION_FILTER = "NUTRITION";
	public static final String SHELF_EDGE_FILTER = "SHELF-EDGE";

	@Autowired
	private transient ProductRepository productRepository;

	@Autowired
	private transient ItemService itemService;

	@Autowired
	private transient UserSearchRepository userSearchRepository;

	@Autowired
	private transient NutritionService nutritionService;

	@Autowired
	private transient ShelfEdgeService shelfEdgeService;

	@Autowired
	private transient ObjectMapper objectMapper;

	/**
	 * Searches for products based on search criteria and will write them out to a stream as they are found.
	 *
	 * @param searchCriteria The search criteria to use when searching for product.
	 * @param filters The filters to use to determine what data to send back.
	 * @param outputStream The output stream to write the products to.
	 * @throws IOException
	 */
	public void streamProductsBySearchCriteria(SearchCriteria searchCriteria, List<String> filters, OutputStream outputStream) throws IOException {

		try (StreamingOutputWriter<Product> streamingOutputWriter = new StreamingOutputWriter<>(this.objectMapper, outputStream)) {
			searchForProductsAndApplyConsumer(searchCriteria, filters, streamingOutputWriter::write);
		}
	}

	/**
	 * Searches for products based on search criteria.
	 *
	 * @param searchCriteria The search criteria to use when searching for product.
	 * @param filters The filters to use to determine what data to send back.
	 */
	public List<Product> getProductsBySearchCriteria(SearchCriteria searchCriteria, List<String> filters) {

		List<Product> products = new LinkedList<>();
		this.searchForProductsAndApplyConsumer(searchCriteria, filters, products::add);
		return products;
	}

	/**
	 * Common function to search for products and apply a consumer to the products generated (e.g. add to a list
	 * or write to an output stream).
	 *
	 * @param searchCriteria The search criteria to use when searching for product.
	 * @param filters The filters to use to determine what data to send back.
	 * @param consumer The consumer to apply to each product found.
	 */
	private void searchForProductsAndApplyConsumer(SearchCriteria searchCriteria, List<String> filters, Consumer<Product> consumer) {

		for (Long productId : searchCriteria.getProductIds()) {
			this.getProductById(productId, filters)
					.filter(p -> applyNutritionCriterion(p, searchCriteria.getHasNutrition()))
					.ifPresent(consumer);
		}
	}

	/**
	 * Returns a Product object for a given productId.
	 *
	 * @param productId The ID of the product to look for.
	 * @param filters A list of filters to apply to the data to return.
	 * @return The Product with the requested ID. Will return null if one does not exist.
	 */
	public Optional<Product> getProductById(Long productId, List<String> filters) {

		return this.productRepository.findById(productId).map((p) -> this.productMasterToProduct(p, filters));
	}

	/**
	 * Takes a ProductMaster and convert it to a Product.
	 *
	 * @param productMaster The ProductMaster to convert.
	 * @return The converted Product.
	 */
	protected Product productMasterToProduct(ProductMaster productMaster, List<String> filters) {
		if (Objects.isNull(productMaster)) {
			return null;
		}
		Product toReturn = this.productMasterToSkinnyProduct(productMaster);

		this.applySupplyChainFields(toReturn, productMaster, filters);
		this.applyEcommerceFields(toReturn, productMaster, filters);
		this.applyNutritionFields(toReturn, productMaster, filters);
		this.applyShelfEdgeFields(toReturn, productMaster, filters);

		return toReturn;
	}


	/**
	 * Converts a ProductMaster to a Product.
	 *
	 * @param productMaster The ProductMaster to convert.
	 * @return The converted Product. If productMaster is null, will return null.
	 */
	public Product productMasterToSkinnyProduct(ProductMaster productMaster) {

		if (Objects.isNull(productMaster)) {
			return null;
		}

		logger.debug(String.format("Converting product %d.", productMaster.getProdId()));

		return Product.of().setProductId(productMaster.getProdId())
				.setProductDescription(productMaster.getDescription())
				.setPrimaryScanCodeId(productMaster.getPrimaryScanCodeId());
	}


	/**
	 * Returns whether or not a product should be returned based on the user's request to see nutrition data.
	 *
	 * @param product The product to check.
	 * @param includeNutrition Whether or not the user asked to filter out products with or without nutrition.
	 * @return True if the product meets the user's search criteria and false otherwise.
	 */
	protected static boolean applyNutritionCriterion(Product product, Boolean includeNutrition) {

		if (Objects.isNull(includeNutrition)) {
			return true;
		}

		boolean hasNutrition = ListUtils.notEmpty(product.getNutritionPanels());

		return includeNutrition == hasNutrition;
	}

	/**
	 * Handles processing the supply-chain related fields.
	 *
	 * @param product The product to apply the fields to. This object may be modified by the method.
	 * @param productMaster The ProductMaster to pull data from.
	 * @param filters The filters the user asked to be applied.
	 */
	private void applySupplyChainFields(Product product, ProductMaster productMaster, List<String> filters) {

		if (this.excludeFields(SUPPLY_CHAIN_FILTER, filters)) {
			return;
		}

		List<Item> items = productMaster.getProdItems().stream()
				.filter(im -> im.getItemMaster().getKey().isWarehouse())
				.map(this::extractItemMasterAndConvert).collect(Collectors.toList());
		if (ListUtils.notEmpty(items)) {
			product.setItems(items);
		}
	}

	/**
	 * Handles processing the shelf-edte related fields.
	 *
	 * @param product The product to apply the fields to. This object may be modified by the method.
	 * @param productMaster The ProductMaster to pull data from.
	 * @param filters The filters the user asked to be applied.
	 */
	private void applyShelfEdgeFields(Product product, ProductMaster productMaster, List<String> filters) {

		if (this.excludeFields(SHELF_EDGE_FILTER, filters)) {
			return;
		}

		this.shelfEdgeService.applyShelfEdgeAttributes(product, productMaster);
	}

	/**
	 * Handles processing the ecommerce related fields.
	 *
	 * @param product The product to apply the fields to. This object may be modified by the method.
	 * @param productMaster The ProductMaster to pull data from.
	 * @param filters The filters the user asked to be applied.
	 */
	private void applyEcommerceFields(Product product, ProductMaster productMaster, List<String> filters) {

		if (this.excludeFields(ECOMMERCE_FILTER, filters)) {
			return;
		}

		if (Objects.nonNull(productMaster.getPhCommodity().getEbmId())) {

			String userName;
			try {
				List<User> userList =
						this.userSearchRepository.getUserList(List.of(productMaster.getPhCommodity().getEbmId()));
				if (CollectionUtils.isEmpty(userList)) {
					userName = UNKNOWN_USER_NAME;
				} else {
					if (userList.size() > MAX_USERS_PER_ID) {
						logger.warn(String.format("Found multiple users for %s, assuming first is correct.",
								productMaster.getPhCommodity().getEbmId()));
					}
					userName = userList.get(0).getFullName();
				}

			} catch (Exception e) {
				logger.error(String.format("Error fetching user name: %s", e.getLocalizedMessage()));
				userName = UNKNOWN_USER_NAME;
			}
			product.setEbm(String.format(NAME_AND_CODE_FORMAT, userName, productMaster.getPhCommodity().getEbmId()));
		}
	}

	/**
	 * Handles processing the nutrition related fields.
	 *
	 * @param product The Product to add nutrition data to.
	 * @param productMaster The ProductMaster for the prodct to process.
	 * @param filters The filters the user asked to be applied.
	 */
	private void applyNutritionFields(Product product, ProductMaster productMaster, List<String> filters) {

		if (this.excludeFields(NUTRITION_FILTER, filters)) {
			return;
		}

		try {
			this.nutritionService.populateNutrition(product, productMaster);
		} catch (Exception e) {
			logger.error(String.format("Error getting nutrition for product %d: %s", productMaster.getProdId(), e.getLocalizedMessage()));
		}
	}

	/**
	 * Convenience method that will help determine which collection of fields to return in a response.
	 *
	 * @param filterName The filter to see if it should be applied.
	 * @param filters The list of filters sent by the client. This may be null or empty. The function assumes
	 *                null or empty lists mean send everything.
	 * @return True if the fields should be excluded and false otherwise.
	 */
	private boolean excludeFields(String filterName, List<String> filters) {
		return !(CollectionUtils.isEmpty(filters) || filters.contains(filterName));
	}

	/**
	 * A convenience method that will pull an ItemMaster out of a ProdItem and convert it to an Item.
	 *
	 * @param prodItem The ProdItem to pull the ItemMaster from.
	 * @return The converted Item.
	 */
	private Item extractItemMasterAndConvert(ProdItem prodItem) {
		return this.itemService.itemMasterToItem(prodItem.getItemMaster());
	}

}
