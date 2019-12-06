/*
 * ActivationMessageTibcoSender
 *
 *  Copyright (c) 2019 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */
package com.heb.pm.jms;

import com.heb.pm.dao.core.entity.SourceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This is template for Activation.
 */
@Service
public class ActivationMessageTibcoSender {

    private static final Logger logger = LoggerFactory.getLogger(ActivationMessageTibcoSender.class);

    /**
     * DATE_YYYYMMDDHHMMSSSS.
     */
    public static final String DATE_YYYYMMDDHHMMSSSS = "yyyy-MM-dd'-'HH.mm.ss.SSSSSS";

    /**
     * STRING_SRC_SYSTEM.
     */
    public static final String STRING_SRC_SYSTEM = String.valueOf(SourceSystem.PAM_SOURCE_SYSTEM);

    /**
     * SPACE.
     */
    public static final String SPACE = " ";

	@Autowired(required = false)
	@Qualifier("tibcoJmsTemplate")
	private transient JmsTemplate jmsTemplate;

    /**
     * Send message to JMS Queue.
     * @param message String
     * @author vn55306
     */
    public void sendMesageToJMSQueue(final String message) {
        logger.info("sendMesageToJMSQueue Message = " + message);

        try {
            if (jmsTemplate == null) {
                logger.error("jmsTemplate is null, unable to put message on queue");
            } else {
                jmsTemplate.send(session -> {
                    TextMessage textMessage = session.createTextMessage(message);
                    return textMessage;
                });
            }
        } catch (JmsException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Send TrackingId To TIBCO EMS Queue.
     * @param trkId Tracking ID
     * @param countWorkIds count of products in transaction
     * @param userId user requesting it
     * @author vn55306
     */
    public void sendTrkIdToTibcoEMSQueue(final long trkId, final int countWorkIds, final String userId) {
        logger.info("sendTrkIdToTibcoEMSQueue trkId = " + trkId + " countWorkIds = " + countWorkIds + " userId = " + userId);

        String createTs = getCurrentTime();
        String dataMessage = formatIntToString(Math.toIntExact(trkId)) + formatIntToString(countWorkIds) + STRING_SRC_SYSTEM + userId + SPACE + createTs;

        this.sendMesageToJMSQueue(dataMessage);
    }

    /**
     * Return the current time to put in the message for the queue.
     * @return time
     */
    private String getCurrentTime() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat(DATE_YYYYMMDDHHMMSSSS, Locale.getDefault());
        return formatter.format(time);
    }

    /**
     * Format Int To String of 9 bytes with leading 0's.
     * @param num Number
     * @return String
     * @author vn55306
     */
    private String formatIntToString(final int num) {
        return String.format("%09d", num);
    }
}
