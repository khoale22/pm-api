/*
 *  com.heb.pm.api.version.VersionEndpoint
 *
 *  Copyright (c) 2018 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of HEB.
 */
package com.heb.pm.version;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * A REST endpoint that will return the versions of this API that are available.
 *
 * @author d116773
 * @since 1.0.0
 */
@RestController
@RequestMapping(VersionEndpoint.VERSION_BASE_URL)
@Api(value = "VersionAPI", produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionEndpoint {

	private static final Logger logger = LoggerFactory.getLogger(VersionEndpoint.class);

	protected static final String VERSION_BASE_URL = "/versions";

	@Autowired(required = false)
	private transient BuildProperties buildProperties;

	/**
	 * Returns the current version of this API.
	 *
	 * @return The current version of this API.
	 */
	@ApiOperation("Returns the application's current version.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "version\" : {version}", response = Version.class)})
	@RequestMapping(method = RequestMethod.GET, value = "/current")
	public Version getCurrentVersion() {

		if (Objects.nonNull(this.buildProperties)) {
			try {
				return Version.of(this.buildProperties.getVersion(), this.buildProperties.get("appRelease"));
			} catch (Exception e) {
				logger.debug("Running outside built environment, version information unavailable.");
				return null;
			}
		} else {
			logger.error("ApplicationContext unavailable.");
			return null;
		}
	}
}
