package com.heb.pm.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heb.pm.core.exception.ValidationException;
import com.heb.pm.core.repository.CandidateTransactionRepository;
import com.heb.pm.core.service.validators.CandidateValidatorRegistry;
import com.heb.pm.core.service.validators.CandidateValidatorType;
import com.heb.pm.dao.core.entity.*;
import com.heb.pm.jms.ActivationMessageTibcoSender;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Candidate service to create handle candidate activation requests.
 *
 * @author a786878
 * @since 1.0.0
 */
@Service
public class CandidateService {

	private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

	private static final int ERRORS_MAX_LENGTH = 4000;

	private static final int MAX_USER_NAME_LENGTH = 20;

	public static final int ONE = 1;

	@Autowired
	private transient CandidateTransactionRepository candidateTransactionRepository;

	@Autowired
	private transient ObjectMapper objectMapper;

	@Autowired
	private transient CandidateValidator candidateValidator;

	@Autowired
	private transient CandidateProductService candidateProductService;

	@Autowired
	private transient CandidateItemService candidateItemService;

	@Autowired(required = false)
	private transient ActivationMessageTibcoSender activationMessageTibcoSender;

	@Value("${tibco.activation.by.queue}")
	private transient boolean useQueue;

	@Autowired
	private transient CandidateValidatorRegistry candidateValidatorRegistry;

	/**
	 * Class to keep PMD from complaining about too many parameters.
	 */
	@Data
	public static class ActivationRequest {

		private final String user;
		private final String responseUrl;
		private final Candidate candidate;

		/**
		 * Constructs a new CandidateActivationRequest.
		 *
		 * @param user The ID of the user who requested activation.
		 * @param responseUrl The URL to send the response back to.
		 * @param candidate The candidate to activate.
		 */
		public ActivationRequest(String user, String responseUrl, Candidate candidate) {
			this.user = StringUtils.truncate(user, MAX_USER_NAME_LENGTH);
			this.responseUrl = responseUrl;
			this.candidate = candidate;
		}
	}

	/**
	 * Activate the candidate.
	 *
	 * @param activationRequest The ActivationRequest.
     * @return A unique ID identifying this request.
	 */
	@Transactional
    public String activateCandidate(ActivationRequest activationRequest) {

 		logger.debug(String.format("Activating candidate %d: \"%s\".", activationRequest.getCandidate().getCandidateId(),
				activationRequest.getCandidate().getDescription()));

		String uuid = UUID.randomUUID().toString();

		// Default non-set true/false values.
		this.handleEmptyTrueFalse(activationRequest.getCandidate());

		// See if the candidate has errors.
		List<String> errors = this.candidateValidator.validate(activationRequest.getCandidate());

		// If it fails validation, save an error request/transaction and throw an error.
		if (!errors.isEmpty()) {

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Found %d errors with the candidate.", errors.size()));
				errors.forEach(logger::debug);
			}
			this.saveErrorTransaction(activationRequest, uuid, errors);
			throw new ValidationException("Unable to validate candidate.", errors);
		}

		// If it passes, build the ps_ structure and save it.
		CandidateTransaction candidateTransaction = this.buildCandidateTransaction(activationRequest, uuid);
		this.candidateTransactionRepository.save(candidateTransaction);

		// Put the message on the queue to kick off activation job
		if (useQueue) {
			activationMessageTibcoSender.sendTrkIdToTibcoEMSQueue(candidateTransaction.getTrackingId(), candidateTransaction.getCandidateWorkRequests().size(), activationRequest.getUser());
		}

		logger.debug(String.format("Returning request ID %s. ", uuid));

		return uuid;
	}

	/**
	 * Builds an candidate transaction and activation request in an error state and saves it. The saving has to be pulled
	 * out of the regular flow (and the actuall call in another class) so it coule be outside of the regular transaction
	 * since I want to throw an exception when there are validation exceptions.
	 *
	 * @param activationRequest The ActivationRequest.
	 * @param id The ID of the request.
	 */
	protected void saveErrorTransaction(ActivationRequest activationRequest, String id, List<String> errors) {

		CandidateTransaction candidateTransaction = new CandidateTransaction();
		candidateTransaction.setSource(SourceSystem.PAM_SOURCE_SYSTEM.toString())
				.setCreateDate(Instant.now())
				.setCreateUserId(activationRequest.getUser())
				.setUserRole(CandidateTransaction.ROLE_USER)
				.setFileNm(id)
				.setFileDescription(activationRequest.getCandidate().getDescription())
				.setTransactionStatus(CandidateTransaction.STAT_CODE_COMPLETE)
				.getCandidateActivationRequest().add(this.candidateToCandidateActivationRequest(activationRequest, id, CandidateWorkRequest.STATUS_FAILED,
						errors, candidateTransaction));
		this.candidateValidator.saveErrorTransaction(candidateTransaction);
	}


	/**
	 * Constructs a CandidateTransaction fully populated with all the ps_ tables.
	 *
	 * @param activationRequest The ActivationRequest.
	 * @param id The ID of the request.
	 * @return A CandidateTransaction fully populated with all the data for the ps_ tables.
	 */
	protected CandidateTransaction buildCandidateTransaction(ActivationRequest activationRequest, String id) {

		CandidateTransaction candidateTransaction = this.candidateToCandidateTransaction(activationRequest, id);

		List<CandidateWorkRequest> candidateWorkRequests =  activationRequest.getCandidate().getCandidateProducts().stream()
				.map((p) -> {
					CandidateWorkRequest candidateWorkRequest = this.candidateProductToCandidateWorkRequest(activationRequest, candidateTransaction, p);
					candidateWorkRequest.getCandidateProductMasters().add(
							this.candidateProductService.buildCandidateProductMaster(activationRequest.getUser(),
									candidateWorkRequest, activationRequest.getCandidate(), p));

					candidateWorkRequest.getCandidateItemMasters()
							.add(this.candidateItemService.buildCandidateItemMaster(activationRequest.getUser(),
									candidateWorkRequest, activationRequest.getCandidate(), p));

					candidateWorkRequest.getCandidateStatuses().add(this.candidateToCandidateStatus(activationRequest.getUser(), candidateWorkRequest));
					return candidateWorkRequest;

				})
				.collect(Collectors.toList());

		candidateTransaction.setCandidateWorkRequests(candidateWorkRequests);

		candidateTransaction.getCandidateActivationRequest().add(this.candidateToCandidateActivationRequest(activationRequest, id,
				CandidateWorkRequest.STATUS_WORKING, null, candidateTransaction));

		return candidateTransaction;
	}

	/**
	 * Constructs a new CandidateActivationRequest.
	 *
	 * @param activationRequest The ActivationRequest.
	 * @param id The ID of the request.
	 * @param status The status of the request.
	 * @param errors Any errors to add.
	 * @param candidateTransaction The CandidateTransaction to tie this request to.
	 * @return A new CandidateActivationRequest.
	 */
	protected CandidateActivationRequest candidateToCandidateActivationRequest(ActivationRequest activationRequest, String id,
																			   String status, List<String> errors,
																			   CandidateTransaction candidateTransaction) {

		String candidateAsJson;
		try {
			candidateAsJson = this.objectMapper.writeValueAsString(activationRequest.getCandidate());
		} catch (JsonProcessingException e) {
			logger.error(e.getLocalizedMessage());
			candidateAsJson = e.getLocalizedMessage();
		}

		CandidateActivationRequest candidateActivationRequest = new CandidateActivationRequest();
		candidateActivationRequest
				.setCandidate(candidateAsJson)
				.setRequestId(id)
				.setResponseURL(activationRequest.getResponseUrl())
				.setStatus(status)
				.setCreateId(activationRequest.getUser())
				.setCreateTime(Instant.now())
				.setCandidateTransaction(candidateTransaction);
		if (Objects.nonNull(errors)) {
			StringJoiner errorsJoiner = new StringJoiner(" ");
			errors.forEach(errorsJoiner::add);
			String errorText = errorsJoiner.toString();
			candidateActivationRequest.setErrors(StringUtils.abbreviate(errorText, ERRORS_MAX_LENGTH));
		}
		return candidateActivationRequest;
	}

	/**
	 * Converts the Candidate to a CandidateTransaction.
	 *
	 * @param activationRequest The ActivationRequest.
	 * @param id The ID of the request.
	 * @return The CandidateTransaction.
	 */
	protected CandidateTransaction candidateToCandidateTransaction(ActivationRequest activationRequest, String id) {

		CandidateTransaction candidateTransaction = new CandidateTransaction();
		candidateTransaction.setSource(SourceSystem.PAM_SOURCE_SYSTEM.toString())
				.setCreateDate(Instant.now())
				.setCreateUserId(activationRequest.getUser())
				.setUserRole(CandidateTransaction.ROLE_USER)
				.setFileNm(id)
				.setFileDescription(activationRequest.getCandidate().getDescription())
				.setTransactionStatus(CandidateTransaction.STAT_CODE_NOT_COMPLETE);

		return candidateTransaction;
	}

	/**
	 * Converts a Candidate to a CandidateWorkRequest.
	 *
	 * @param activationRequest The ActivationRequest.
	 * @param candidateTransaction The CandidateTransaction this work request will be a part of.
	 * @param candidateProduct The CandidateProduct being processed.
	 * @return The CandidateWorkRequest,
	 */
	protected CandidateWorkRequest candidateProductToCandidateWorkRequest(ActivationRequest activationRequest, CandidateTransaction candidateTransaction,
																		CandidateProduct candidateProduct) {

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest();
		candidateWorkRequest.setCandidateTransaction(candidateTransaction)
				.setIntent(CandidateWorkRequest.INTENT_NEW_PRODUCT)
				.setStatus(CandidateWorkRequest.STATUS_WORKING)
				.setCreateUserId(activationRequest.getUser())
				.setSource(SourceSystem.PAM_SOURCE_SYSTEM)
				.setLastUpdateUserId(activationRequest.getUser())
				.setVendorEmail(activationRequest.getCandidate().getContactEmail())
				.setUpc(candidateProduct.getUpc())
				.setStatusChangeReason(CandidateStatus.STATUS_CHANGE_REASON_WORKING)
				.setReadyToActivate(CandidateUtils.YES)
		;

		this.handlePhoneAndAreaCode(candidateWorkRequest, activationRequest.getCandidate().getContactNumber());

		return candidateWorkRequest;
	}

	/**
	 * Creates a CandidateStatus record for the new candidate.
	 *
	 * @param user The user who kicked off activation.
	 * @param candidateWorkRequest The CandidateWorkRequest to add this CandidateStatus to.
	 * @return The CandidateStatus for this candidate.
	 */
	protected CandidateStatus candidateToCandidateStatus(String user, CandidateWorkRequest candidateWorkRequest) {

		CandidateStatus candidateStatus = new CandidateStatus();
		candidateStatus.getKey()
				.setLastUpdateDate(Instant.now())
				.setStatus(CandidateWorkRequest.STATUS_WORKING);

		candidateStatus.setUpdateUserId(user)
				.setStatusChangeReason(CandidateStatus.STATUS_CHANGE_REASON_WORKING)
				.setCandidateWorkRequest(candidateWorkRequest);

		return candidateStatus;
	}

	/**
	 * Handles parsing the area code and phone number from the candidate in a way that will fit into the candidate work request.
	 * This method may modify the CandidateWorkRequest passed in.
	 *
	 * @param candidateWorkRequest The CandidateWorkRequest to set the area codce and phone number for.
	 * @param phoneNumber The phone number from the candidate.
	 */
	protected void handlePhoneAndAreaCode(CandidateWorkRequest candidateWorkRequest, String phoneNumber) {

		if (Objects.isNull(phoneNumber)) {
			return;
		}

		// If it's not in the right format, then just ignore it.
		String flatPhoneNumber = phoneNumber.replaceAll("[\\s.()-]", "");
		if (!flatPhoneNumber.matches("\\d{10}")) {
			return;
		}

		String numParserRegex = "(\\d{3})(\\d{3})(\\d{4})";
		Pattern pattern = Pattern.compile(numParserRegex);
		Matcher matcher = pattern.matcher(flatPhoneNumber);

		if (matcher.matches()) {
			candidateWorkRequest.setVendorAreaCode(matcher.group(1))
					.setVendorPhone(String.format("%s%s", matcher.group(2), matcher.group(3)));
		}
	}

	/**
	 * When the candidate is sent with null for some of the true/false attributes, this method will
	 * set all of those to false.
	 *
	 * @param candidate The candidate to validate. This parameter may be updated.
	 */
	private void handleEmptyTrueFalse(Candidate candidate) {

		if (Objects.isNull(candidate.getTaxable())) {
			candidate.setTaxable(Boolean.FALSE);
		}

		if (Objects.isNull(candidate.getCodeDate())) {
			candidate.setCodeDate(Boolean.FALSE);
		}
	}


	/**
	 * Retutns this class's object mapper.
	 *
	 * @return This class's object mapper.
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * Sets this class's object mapper.
	 *
	 * @param objectMapper This class's object mapper.
	 * @return This object for further configuration.
	 */
	public CandidateService setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	/**
	 * Validates the candidate a list of validators.
	 *
	 * @param validatorTypes The list of the types of validators to apply.
	 * @param candidate The candidate to apply the validators to.
	 */
	public void validate(List<CandidateValidatorType> validatorTypes, Candidate candidate) {

		// Use the registry to get a list of validators to apply.
		List<com.heb.pm.core.service.validators.CandidateValidator> validatorsToRun =
				this.candidateValidatorRegistry.getValidators(validatorTypes);

			List<String> errorMessages = new LinkedList<>();

			StringJoiner validatorNames = new StringJoiner(",");

			// Apply each validator in turn, collecting the errors as they are produced.
			for (com.heb.pm.core.service.validators.CandidateValidator validator : validatorsToRun) {

				validatorNames.add(validator.toString());
				try {
					validator.validate(candidate);
				} catch (ValidationException e) {
					errorMessages.addAll(e.getErrors());
				}
			}

			// If there were any errors produced, throw a new error with the full list.
			if (!errorMessages.isEmpty()) {
				throw new ValidationException(String.format("Unable to validate with validators %s.", validatorNames.toString()), errorMessages);
			}
	}
}
