/*
 *  com.heb.pm.NewErrorHandler
 *
 *  Copyright (c) 2016 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of HEB.
 */

package com.heb.pm.error;

import com.heb.pm.core.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces Spring's default error controller (org.springframework.boot.autoconfigure.web.BasicErrorController).
 * This will trim out some of the things that Spring included like the type of error that was thrown (which can
 * help reveal about the internal implementation of the application). It is also better at pulling out the real
 * error message that hte application provides.
 *
 * @author d116773
 * @since 1.0.0
 */
@Controller
public class PmErrorController implements ErrorController {

	private static final Logger logger = LoggerFactory.getLogger(PmErrorController.class);

	private static final String STATUS_CODE_RESPONSE_KEY = "status";
	private static final String ERROR_CODE_RESPONSE_KEY = "error";
	private static final String MESSAGE_CODE_RESPONSE_KEY = "message";
	private static final String STATUS_CODE_ATTRIBUTE_NAME = "javax.servlet.error.status_code";
	private static final String MESSAGE_ATTRIBUTE_NAME = "javax.servlet.error.message";
	private static final String ROOT_ERROR_ATTRIBUTE_NAME = DefaultErrorAttributes.class.getName() + ".ERROR";
	private static final String URI_ATTRIBUTE_NAME = "javax.servlet.error.request_uri";

	private static final String BAD_STATUS_CODE = "Bad status code: %s";
	private static final String NOT_FOUND_ERROR_MESSAGE = "URI not found: %s";
	private static final String UNKNOWN_ERROR = "Unknown Error";

	@Value("${error.path:/error}")
	private transient String errorPath;

	/**
	 * Return the path to the error page.
	 *
	 * @return The path to the error page.
	 */
	@Override
	public String getErrorPath() {
		return this.errorPath;
	}

	/**
	 * Responds to requests for JSON. It will return to the client the following properties in an object:
	 * status - the HTTP status the application is responding with.
	 * error - the description of the HTTP status code.
	 * message - the message from the error that is causing this to be called.
	 *
	 * @param request The HttpServletRequest that is in error.
	 * @return A ResponseEntity with the above parameters as keys in a map and the HTTP status code.
	 */
	@RequestMapping(value = "${error.path:/error}", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> errorAsJson(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		Map<String, Object> body = getErrorAttributes(status, request);
		return new ResponseEntity<>(body, status);
	}

	/**
	 * Responds to requests for HTML. It does not return a nicely formatted HTML page. Rather, it just
	 * takes the same keys as the errorAsJson method creates and turns it into a string.
	 *
	 * @param request The HttpServletRequest that is in error.
	 * @return The same object as errroAsJson but turned into a string.
	 */
	@RequestMapping(value = "${error.path:/error}", produces = "text/html")
	@ResponseBody
	public String errorAsHtml(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		Map<String, Object> body = getErrorAttributes(status, request);
		return this.stringify(body);
	}

	/**
	 * Searches the HttpServletRequest passed in for error messages and adds them along with the status to a map.
	 *
	 * @param status The HttpStatus to add to the map.
	 * @param request The HttpServletRequest that is in error.
	 * @return A map as as described in errorAsJson.
	 */
	private Map<String, Object> getErrorAttributes(HttpStatus status, HttpServletRequest request) {
		Map<String, Object> map = new ConcurrentHashMap<>();

		// Add the status and the text of the status to the return map.
		map.put(PmErrorController.STATUS_CODE_RESPONSE_KEY, status.value());
		map.put(PmErrorController.ERROR_CODE_RESPONSE_KEY, status.getReasonPhrase());

		// Go through the various types of errors and try to extract a meaningful error
		boolean handled = this.handle404(status, request, map);
		handled = handled || this.handleHibernateError(request, map);
		handled = handled || this.handleDataAccessException(request, map);
		handled = handled || this.handleValidationError(request, map);
		if (!handled) {
			this.defaultErrorExtractor(request, map);
		}

		return map;

	}

	/**
	 * Pulls the HttpStatus out of the HttpServletRequest. If it cannot be found, an internal servlet error is returned.
	 *
	 * @param request The HttpServletRequest that is in error.
	 * @return The HttpStatus tied to the error in the request.
	 */
	private HttpStatus getStatus(HttpServletRequest request) {

		// Try to get the status code from the request.
		Object statusCode = request.getAttribute(PmErrorController.STATUS_CODE_ATTRIBUTE_NAME);
		if (statusCode instanceof Integer) {
			try {
				return HttpStatus.valueOf((Integer) statusCode);
			} catch (Exception e) {
				PmErrorController.logger.error(String.format(PmErrorController.BAD_STATUS_CODE, statusCode));
			}
		} else {
			PmErrorController.logger.error(String.format(PmErrorController.BAD_STATUS_CODE, statusCode));
		}
		// Default if the status is not set.
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	/**
	 * Takes the error map and turns it into a String. The String is not formatted prettily.
	 *
	 * @param map The map to turn into a string.
	 * @return A String with the keys and values from the map.
	 */
	private String stringify(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();

		map.forEach((k, v) -> sb.append(k).append('=').append(v).append(';'));

		return sb.toString();
	}

	/**
	 * If the status of the response is 404, this puts a message with the error into the response map.
	 *
	 * @param status The status of the request.
	 * @param request The request itself.
	 * @param errorMap The map to put error messages into.
	 * @return True if the error was handled and false otherwise.
	 */
	private boolean handle404(HttpStatus status, HttpServletRequest request, Map<String, Object> errorMap) {
		if (status.equals(HttpStatus.NOT_FOUND)) {
			errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY,
					String.format(PmErrorController.NOT_FOUND_ERROR_MESSAGE,
							request.getAttribute(PmErrorController.URI_ATTRIBUTE_NAME)));
			return true;
		}
		return false;
	}

	/**
	 * Handles errors thrown by Spring Data.
	 *
	 * @param request The HTTP request that initiated the call.
	 * @param errorMap The map to put error messages into.
	 * @return True if the error was handled and false otherwise.
	 */
	private boolean handleDataAccessException(HttpServletRequest request, Map<String, Object> errorMap) {
		Object rootError = request.getAttribute(PmErrorController.ROOT_ERROR_ATTRIBUTE_NAME);
		if (!(rootError instanceof DataAccessException)) {
			return false;
		}

		DataAccessException dataAccessException =  (DataAccessException) rootError;
		errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY, dataAccessException.getMostSpecificCause().getLocalizedMessage());
		return true;
	}

	/**
	 * Handles errors thrown by update request validation.
	 *
	 * @param request The HTTP request that initiated the call.
	 * @param errorMap The map to put error messages into.
	 * @return True if the error was handled and false otherwise.
	 */
	private boolean handleValidationError(HttpServletRequest request, Map<String, Object> errorMap) {

		Object rootError = request.getAttribute(PmErrorController.ROOT_ERROR_ATTRIBUTE_NAME);
		if (!(rootError instanceof ValidationException)) {
			return false;
		}
		ValidationException validationException = (ValidationException) rootError;
		errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY, validationException.getLocalizedMessage());
		errorMap.put("validationErrors", validationException.getErrors());
		return true;
	}

	/**
	 * Handles errors thrown by Hibernate.
	 *
	 * @param request The HTTP request that initiated the call.
	 * @param errorMap The map to put error messages into.
	 * @return True if the error was handled and false otherwise.
	 */
	private boolean handleHibernateError(HttpServletRequest request, Map<String, Object> errorMap) {

		Object rootError = request.getAttribute(PmErrorController.ROOT_ERROR_ATTRIBUTE_NAME);
		if (!(rootError instanceof PersistenceException)) {
			return false;
		}
		PersistenceException dataException = (PersistenceException) rootError;
		if (dataException.getCause() == null || dataException.getCause().getCause() == null) {
			return false;
		}
		errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY,
				dataException.getCause().getCause().getLocalizedMessage());
		return true;
	}

	/**
	 * If all the other error extractors fail, this one will do its best.
	 *
	 * @param request The request itself.
	 * @param errorMap The map to put error messages int.
	 * @return True if the error was handled and false otherwise.
	 */
	private boolean defaultErrorExtractor(HttpServletRequest request, Map<String, Object> errorMap) {

		// See if there is a Spring error inside the request. If it's there, it'll have a better message.
		// If it isn't, grab the default message.
		Object rootError = request.getAttribute(PmErrorController.ROOT_ERROR_ATTRIBUTE_NAME);
		if (rootError instanceof Throwable) {
			// We've found instances where there's an error, but the message is null, but its toString has the
			// real error.
			String errorMessage = ((Throwable) rootError).getMessage();
			if (errorMessage == null) {
				errorMessage = rootError.toString();
			}
			errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY, errorMessage);
		} else if (request.getAttribute(PmErrorController.MESSAGE_ATTRIBUTE_NAME) != null) {
			errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY,
					request.getAttribute(PmErrorController.MESSAGE_ATTRIBUTE_NAME));
		} else {
			errorMap.put(PmErrorController.MESSAGE_CODE_RESPONSE_KEY, PmErrorController.UNKNOWN_ERROR);
		}
		return true;
	}
}
