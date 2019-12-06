/*
 * UpcController.java
 *
 * Copyright (c) 2018 HEB
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of HEB.
 */
package com.heb.pm.core.endpoint;


import com.heb.pm.core.exception.NotFoundException;
import com.heb.pm.core.model.Upc;
import com.heb.pm.core.model.updates.nutrition.NutrientStatement;
import com.heb.pm.core.service.UpcService;
import com.heb.pm.core.service.nutrition.NutritionService;
import com.heb.pm.core.service.nutrition.NutritionUpdateResponse;
import com.heb.pm.util.security.wsag.ClientInfoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * REST endpoint for UPCs.
 *
 * @author s769046
 * @since 1.0.0
 */
@RestController()
@RequestMapping(UpcController.UPC_BASE_URL)
public class UpcController {

    private static final Logger logger = LoggerFactory.getLogger(UpcController.class);

    private static final String FIND_UPC =
            "Application %s from IP %s has requested information for by UPC: %d";

    protected static final String UPC_BASE_URL = "/upc";

    @Autowired
    private transient UpcService upcService;

    @Autowired
    private transient NutritionService nutritionService;

    @Autowired
    private transient ClientInfoService clientInfoService;

    /**
     * Looks up information for a UPC. If the UPC is not found, will set the return status to 404.
     *
     * @param upc The UPC to look up information for.
     * @param request The HTTP servlet request that initiated the request.
     * @return The UPC requested.
     */
    @RequestMapping(method = RequestMethod.GET, value = "{upc}")
    @ApiOperation("Returns a selling unit linked to the given upc.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "upc\" : {upc}", response = Upc.class),
            @ApiResponse(code = 404, message = "error : UPC not found.")})
    public Upc findUpcInfoByUpc(
            @ApiParam(value = "The UPC code being requested.", required = true)
            @PathVariable("upc") Long upc, HttpServletRequest request) {

        UpcController.logger.info(String.format(UpcController.FIND_UPC, this.clientInfoService.getClientApplicationName(),
                request.getRemoteAddr(), upc));

        return this.upcService.getUpc(upc).orElseThrow(NotFoundException.NOT_FOUND_EXCEPTION_SUPPLIER);
    }


    /**
     * Saves scale nutrition for a UPC.
     *
     * @param upc The UPC to save nutrition for.
     * @param nutrientStatement The nutrition statement to save.
     * @param request The HTTP servlet request that initiated the request.
     * @return A response with the saved nutrient statement ID and, typically, the UPC the nutrition is tied to.
     */
    @PostMapping("/{upc}/nutrition")
    @ApiOperation("Updates a UPC's scale nutrient statement.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "nutrientStatementId", response = NutritionUpdateResponse.class),
            @ApiResponse(code = 409, message = "Unable to validate scale nutrition update request.")
    })
    @PreAuthorize("hasAuthority('UPDATE_NUTRITION')")
    public NutritionUpdateResponse updateScaleNutrition(@ApiParam(value = "The UPC to update the scale nutrition of.", required = true) @PathVariable("upc") Long upc,
                                                        @ApiParam(value = "The UPC's nutrition statement.", required = true)
                       @RequestBody NutrientStatement nutrientStatement, HttpServletRequest request) {

        UpcController.logger.info(String.format("User %s from application %s from IP %s requested to update nutrient statement information for  UPC %d.",
                nutrientStatement.getUserId(), this.clientInfoService.getClientApplicationName(), request.getRemoteAddr(), upc));

        return this.nutritionService.saveNutrientStatement(upc, nutrientStatement);
    }
}
