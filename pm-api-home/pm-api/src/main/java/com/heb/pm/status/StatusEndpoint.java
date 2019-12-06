/*
 *  com.heb.pm.api.status.StatusEndpoint
 *
 *  Copyright (c) 2018 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of HEB.
 */
package com.heb.pm.status;

import com.heb.pm.version.Version;
import com.heb.pm.version.VersionEndpoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint that will return the health of this API. It will return 200 when the API is in a healthy state. Any
 * other response will be considered unhealthy. The status can be changed from 200 by throwing an exception.
 *
 * @author d116773
 * @since 1.0.0
 */
@RestController
@RequestMapping("/status")
@Api(value = "StatusAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatusEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(StatusEndpoint.class);

	@Autowired
	private VersionEndpoint versionEndpoint;

	/**
	 * Checks on the status of the application.
	 *
	 * @return The actual return value is not so important here (the default returns the current version number). The
	 * HTTP status of the response is what is valuable.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/healthy")
	@ApiOperation("Returns current version number. Http status of the response is what is valuable.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "version\" : {version}", response = Version.class),
			@ApiResponse(code = 404, message = "error : Service unavailable.")})
	public Version healthy() {
		logger.debug("Returning OK from the health check");
		return versionEndpoint.getCurrentVersion();
	}

	/**
	 * Indicates when the application is available and ready to take requests.
	 *
	 * @return The actual return value is not so important here (the default returns the current version number). The
	 * HTTP status of the response is what is valuable.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/ready")
	@ApiOperation("Returns current version number. Http status of the response is what is valuable.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "version\" : {version}", response = Version.class),
			@ApiResponse(code = 404, message = "error : Service unavailable.")})
	public Version ready() {
		logger.debug("Returning OK from the ready check");
		return versionEndpoint.getCurrentVersion();
	}

	/**
	 * Returns VersionEndpoint.
	 *
	 * @return The VersionEndpoint.
	 **/
	public VersionEndpoint getVersionEndpoint() {
		return versionEndpoint;
	}

	/**
	 * Sets the VersionEndpoint.
	 *
	 * @param versionEndpoint The VersionEndpoint.
	 **/
	public StatusEndpoint setVersionEndpoint(VersionEndpoint versionEndpoint) {
		this.versionEndpoint = versionEndpoint;
		return this;
	}
}
