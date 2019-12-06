package com.heb.pm.core.service;

import com.heb.pm.config.PmApiConfiguration;
import com.heb.pm.dao.core.entity.CandidateActivationRequest;
import com.heb.pm.dao.core.entity.CandidateStatus;
import com.heb.pm.dao.core.entity.CandidateTransaction;
import com.heb.pm.dao.core.entity.CandidateWorkRequest;
import com.heb.pm.pam.model.Candidate;
import com.heb.pm.pam.model.CandidateProduct;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tests CandidateService.
 *
 * @author d116773
 * @since 1.1.0
 */
public class CandidateServiceTest {

	private static final String USER_ID = "dsfkdsj";

	private final CandidateService candidateService = new CandidateService();
	private final PmApiConfiguration pmApiConfiguration = new PmApiConfiguration();

	@Before
	public void setup() {

		this.candidateService.setObjectMapper(this.pmApiConfiguration.getJacksonBuilder());
	}

	@Test
	public void candidateToCandidateActivationRequest_returnsCandidateActivationRequestNoErrors() {

		Candidate candidate = Candidate.of().setCandidateId(12L);
		CandidateService.ActivationRequest activationRequest = new CandidateService.ActivationRequest(USER_ID, "test", candidate);

		CandidateTransaction candidateTransaction = new CandidateTransaction().setTrackingId(113234L);

		CandidateActivationRequest candidateActivationRequest =
				this.candidateService.candidateToCandidateActivationRequest(activationRequest, "dddd", CandidateWorkRequest.STATUS_WORKING,
						null, candidateTransaction);

		Assert.assertEquals("{\"candidateId\":12,\"candidateProducts\":[]}", candidateActivationRequest.getCandidate());
		Assert.assertEquals("dddd", candidateActivationRequest.getRequestId());
		Assert.assertEquals("test", candidateActivationRequest.getResponseURL());
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, candidateActivationRequest.getStatus());
		Assert.assertEquals(USER_ID, candidateActivationRequest.getCreateId());
		Assert.assertEquals(candidateTransaction, candidateActivationRequest.getCandidateTransaction());
		Assert.assertNull(candidateActivationRequest.getErrors());
	}

	@Test
	public void candidateToCandidateActivationRequest_returnsCandidateActivationRequestWithErrors() {

		Candidate candidate = Candidate.of().setCandidateId(12L);
		CandidateService.ActivationRequest activationRequest = new CandidateService.ActivationRequest(USER_ID, "test", candidate);

		CandidateTransaction candidateTransaction = new CandidateTransaction().setTrackingId(113234L);

		CandidateActivationRequest candidateActivationRequest =
				this.candidateService.candidateToCandidateActivationRequest(activationRequest, "dddd", CandidateWorkRequest.STATUS_WORKING,
						List.of("Test error 1", "Test error 2"), candidateTransaction);

		Assert.assertEquals("{\"candidateId\":12,\"candidateProducts\":[]}", candidateActivationRequest.getCandidate());
		Assert.assertEquals("dddd", candidateActivationRequest.getRequestId());
		Assert.assertEquals("test", candidateActivationRequest.getResponseURL());
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, candidateActivationRequest.getStatus());
		Assert.assertEquals(USER_ID, candidateActivationRequest.getCreateId());
		Assert.assertEquals(candidateTransaction, candidateActivationRequest.getCandidateTransaction());
		Assert.assertEquals("Test error 1 Test error 2", candidateActivationRequest.getErrors());
	}


	@Test
	public void candidateToCandidateTransaction_returnsCandidateTransaction() {
		Candidate candidate = Candidate.of().setCandidateId(12L).setDescription("Test candidate transaction");
		CandidateService.ActivationRequest activationRequest = new CandidateService.ActivationRequest(USER_ID, "test", candidate);

		CandidateTransaction candidateTransaction = this.candidateService.candidateToCandidateTransaction(activationRequest, "ddjwoiejfwe");

		Assert.assertEquals("32", candidateTransaction.getSource());
		Assert.assertEquals(USER_ID, candidateTransaction.getCreateUserId());
		Assert.assertEquals(CandidateTransaction.ROLE_USER, candidateTransaction.getUserRole());
		Assert.assertEquals("ddjwoiejfwe", candidateTransaction.getFileNm());
		Assert.assertEquals("Test candidate transaction", candidateTransaction.getFileDescription());
		Assert.assertEquals(CandidateTransaction.STAT_CODE_NOT_COMPLETE, candidateTransaction.getTransactionStatus());
	}

	@Test
	public void candidateProductToCandidateWorkRequest_returnsCandidateWorkRequest() {

		Candidate candidate = Candidate.of().setCandidateId(12L).setDescription("Test candidate transaction444s")
				.setContactEmail("test@thebtest.com").setContactNumber("(210) 456-9083");
		CandidateService.ActivationRequest activationRequest = new CandidateService.ActivationRequest(USER_ID, "respUrl", candidate);

		CandidateProduct candidateProduct = CandidateProduct.of().setUpc(34454344L);

		CandidateTransaction candidateTransaction = this.candidateService.candidateToCandidateTransaction(activationRequest, "dfaewvveh");

		CandidateWorkRequest candidateWorkRequest = this.candidateService.candidateProductToCandidateWorkRequest(activationRequest, candidateTransaction, candidateProduct);

		Assert.assertEquals(Long.valueOf(CandidateWorkRequest.INTENT_NEW_PRODUCT), candidateWorkRequest.getIntent());
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, candidateWorkRequest.getStatus());
		Assert.assertEquals(USER_ID, candidateWorkRequest.getCreateUserId());
		Assert.assertEquals(Long.valueOf(32L), candidateWorkRequest.getSource());
		Assert.assertEquals(USER_ID, candidateWorkRequest.getLastUpdateUserId());
		Assert.assertEquals("test@thebtest.com", candidateWorkRequest.getVendorEmail());
		Assert.assertEquals("210", candidateWorkRequest.getVendorAreaCode());
		Assert.assertEquals("4569083", candidateWorkRequest.getVendorPhone());
		Assert.assertEquals(Long.valueOf(CandidateStatus.STATUS_CHANGE_REASON_WORKING), candidateWorkRequest.getStatusChangeReason());
		Assert.assertEquals(CandidateUtils.YES, candidateWorkRequest.getReadyToActivate());
		Assert.assertEquals(candidateTransaction, candidateWorkRequest.getCandidateTransaction());
	}

	@Test
	public void candidateToCandidateStatus_returnsCandidateStatus() {

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest().setWorkRequestId(22222L);

		CandidateStatus candidateStatus = this.candidateService.candidateToCandidateStatus(USER_ID, candidateWorkRequest);

		Assert.assertEquals(candidateWorkRequest, candidateStatus.getCandidateWorkRequest());
		Assert.assertEquals(CandidateWorkRequest.STATUS_WORKING, candidateStatus.getKey().getStatus());
		Assert.assertEquals(Long.valueOf(CandidateStatus.STATUS_CHANGE_REASON_WORKING), candidateStatus.getStatusChangeReason());
		Assert.assertEquals(USER_ID, candidateStatus.getUpdateUserId());
	}

	@Test
	public void handlePhoneAndAreaCode_pamStandard() {
		String phoneNumber = "(555) 111-5555";

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest();
		this.candidateService.handlePhoneAndAreaCode(candidateWorkRequest, phoneNumber);
		Assert.assertEquals("555", candidateWorkRequest.getVendorAreaCode());
		Assert.assertEquals("1115555", candidateWorkRequest.getVendorPhone());
	}

	@Test
	public void handlePhoneAndAreaCode_doesNotDieFromBadData() {
		String phoneNumber = "dfadsafdsfafddsf";

		CandidateWorkRequest candidateWorkRequest = new CandidateWorkRequest();
		this.candidateService.handlePhoneAndAreaCode(candidateWorkRequest, phoneNumber);
		Assert.assertNull(candidateWorkRequest.getVendorAreaCode());
		Assert.assertNull(candidateWorkRequest.getVendorPhone());
	}
}
