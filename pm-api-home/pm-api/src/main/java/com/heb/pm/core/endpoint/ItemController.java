/*
 * ItemController.java
 *
 * Copyright (c) 2018 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.pm.core.endpoint;

import com.heb.pm.core.exception.NotFoundException;
import com.heb.pm.core.model.Item;
import com.heb.pm.core.model.audit.ItemAudit;
import com.heb.pm.core.service.ItemAuditService;
import com.heb.pm.core.service.ItemService;
import com.heb.pm.dao.core.entity.ItemMasterKey;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Rest endpoint for finding item information.
 */
@RestController()
@RequestMapping(ItemController.ITEM_CODE_BASE_URL)
@Api(value = "ItemAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {

	private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

	protected static final String ITEM_CODE_BASE_URL = "/item";

	private static final String BY_ITEM_CODE_URL = "{itemCode}";
	private static final String AUDIT_BY_ITEM_CODE_URL = "{itemCode}/audit";

	private static final String FIND_ITEM_INFO_BY_ITEM_CD =
			"IP %s has requested item information for item code: %d";
	private static final String FIND_ITEM_AUDIT_INFO_BY_ITEM_CD =
			"IP %s has requested item audit information by item code: %d.";

	@Autowired
	private transient ItemService itemService;

	@Autowired
	private transient ItemAuditService itemAuditService;

	/**
	 *  Returns warehouse item information for a given item code. This will set the HTTP status to 404 if not found.
	 *
	 * @param itemCode The item code being requested.
	 * @param request The HTTP servlet request that initiated the request.
	 * @return The item information for the requested item.
	 */
	@RequestMapping(method = RequestMethod.GET, value = BY_ITEM_CODE_URL)
	@ApiOperation("Returns warehouse item information linked to the given item code.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "itemCode\" : {itemCode}", response = Item.class),
			@ApiResponse(code = 404, message = "error : Item code not found.")})
	public Item getItemInfo(
			@ApiParam("The item code being requested.")
			@PathVariable("itemCode") Long itemCode,
			HttpServletRequest request) {

		ItemController.logger.info(String.format(ItemController.FIND_ITEM_INFO_BY_ITEM_CD, request.getRemoteAddr(), itemCode));

		return this.itemService.getItem(itemCode).orElseThrow(NotFoundException.NOT_FOUND_EXCEPTION_SUPPLIER);
	}

	/**
	 * Finds warehouse item audit by item code. If there are no item master audits related to the given item code,
	 * this method will return a response code of 404. Else return the item master audit.
	 *
	 * @param itemCode Item id to search for.
	 * @param request The Http request.
	 * @param response The Http response.
	 * @return Item Audit related to given id and type or null if no item master audits are found.
	 */
	@RequestMapping(method = RequestMethod.GET, value = AUDIT_BY_ITEM_CODE_URL)
	@ApiOperation("Returns warehouse item audit information linked to the given item code.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "itemCode\" : {itemCode}", response = Item.class),
			@ApiResponse(code = 404, message = "error : Item code not found.")})
	public ItemAudit getItemAuditInfo(
			@ApiParam("The item code for the requested item audit.")
			@PathVariable("itemCode") Long itemCode,

			@ApiParam("Filter names of the fields that the user wants removed from results. " +
					"If no filters are included, then all fields are " +
					"returned. If any are included, only the requested fields are returned." +
					" E.G. 'HIVEMIND'")
			@RequestParam(value = "filters", required = false)
					List<String> filters,

			HttpServletRequest request, HttpServletResponse response) {

		ItemController.logger.info(String.format(ItemController.FIND_ITEM_AUDIT_INFO_BY_ITEM_CD, request.getRemoteAddr(), itemCode));

		// Hardcoded to warehouse till service is updated to handle DSD/BOTH.
		ItemAudit itemAudit = this.itemAuditService.findByItemIdAndItemType(itemCode, ItemMasterKey.WAREHOUSE, filters);
		if (itemAudit == null) {
			NotFoundException.NOT_FOUND_EXCEPTION_SUPPLIER.get();
		}
		return itemAudit;
	}
}
