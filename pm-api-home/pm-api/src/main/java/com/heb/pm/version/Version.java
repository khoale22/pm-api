/*
 *  com.heb.pm.api.version.Version
 *
 *  Copyright (c) 2018 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of HEB.
 */

package com.heb.pm.version;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * Represents a version of the API.
 *
 * @author d116773
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public final class Version {

	public static final LocalDate NEVER = LocalDate.of(9999, 12, 31);

	@ApiModelProperty("The version number of the API.")
	private final String number;

	@ApiModelProperty("The status of the API version.")
	private final VersionStatus status;

	@ApiModelProperty("The date this version will be retired.")
	private final LocalDate invalidationDate;

	@ApiModelProperty("The build number of the API.")
	private final String buildNumber;

	/**
	 * Returns a new Version. This should be used for active versions.
	 *
	 * @param number The version number of the API.
	 */
	public static Version of(String number) {
		return new Version(number, VersionStatus.ACTIVE, NEVER, null);
	}

	/**
	 * Returns a new Version. This should be used for active versions when the build number is known.
	 *
	 * @param number The version number of the API.
	 * @param buildNumber The build number of the API.
	 */
	public static Version of(String number, String buildNumber) {
		return new Version(number, VersionStatus.ACTIVE, NEVER, buildNumber);
	}

	/**
	 * Returns a new Version. This should be used for deprecated versions.
	 *
	 * @param number The version number of the API.
	 * @param invalidationDate The date this version will be retired.
	 */
	public static Version of(String number, LocalDate invalidationDate) {
		return new Version(number, VersionStatus.DEPRECATED, invalidationDate, null);
	}

	/**
	 * Constructs a new Version.
	 *
	 * @param number The version number of the API.
	 * @param status The status of the API version.
	 * @param invalidationDate The date this version will be retired.
	 * @param buildNumber The build number of the API.
	 */
	private Version(String number, VersionStatus status, LocalDate invalidationDate, String buildNumber) {
		this.number = number;
		this.status = status;
		this.invalidationDate = invalidationDate;
		this.buildNumber = buildNumber;
	}
}
