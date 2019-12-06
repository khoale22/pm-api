/*
 * ApplicationJmsConfiguration
 *
 *  Copyright (c) 2017 HEB
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information
 *  of HEB.
 */

package com.heb.pm.jms;

import com.tibco.tibjms.TibjmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * The type jms configuration.
 *
 */
@Profile({"cert", "prod"})
@Configuration
@EnableJms
public class TibcoJmsConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(TibcoJmsConfiguration.class);
	/**
	 * The Outbound url.
	 */
	 @Value("${tibco.activation.url}")
	 private transient String outboundUrl;
	 /**
	 * The Outbound user id.
	 */
	 @Value("${tibco.activation.userName}")
	 private transient String outboundUserId;
	 /**
	 * The Outbound password.
	 */
	 @Value("${tibco.activation.password}")
	 private transient String outboundPassword;
	 /**
	 * The Outbound status queue.
	 */
	 @Value("${tibco.activation.queueName}")
	 private transient String outboundStatusQueue;

	/**
	 * The connection factory to the queue. At this time there is one server with the same username and password so only one connection
	 * is needed.
	 *
	 * @return the caching connection factory
	 * @throws JMSException the jms exception
	 * @author vn55306
	 */
	@Bean(name = "tibcoJmsCachingConnectionFactory")
	@Primary
	public CachingConnectionFactory cachingConnectionFactory() throws JMSException {
		LOGGER.info("Configuring caching connection TibcoJmsConfiguration with TIBCO connection factory...");
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		TibjmsConnectionFactory tibjmsConnectionFactory =  new TibjmsConnectionFactory();
		tibjmsConnectionFactory.setServerUrl(this.outboundUrl);
		tibjmsConnectionFactory.setUserName(this.outboundUserId);
		tibjmsConnectionFactory.setUserPassword(this.outboundPassword);
		cachingConnectionFactory.setTargetConnectionFactory(tibjmsConnectionFactory);
		cachingConnectionFactory.setReconnectOnException(true);
		return cachingConnectionFactory;
	}

	/**
	 * Outbound status jms template jms template. This is the default spring jms template. The default queue is the outbound queue.
	 *
	 * @param cachingConnectionFactory the caching connection factory
	 * @return the jms template
	 * @throws JMSException the jms exception
	 * @author vn55306
	 */
	@Bean(name = "tibcoJmsTemplate")
	public JmsTemplate outboundStatusJmsTemplate(@Qualifier("tibcoJmsCachingConnectionFactory")ConnectionFactory cachingConnectionFactory) throws JMSException {
		LOGGER.info("Creating JmsTemplate for the outbound message queue...");
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(cachingConnectionFactory);
		jmsTemplate.setDefaultDestinationName(this.outboundStatusQueue);
		return jmsTemplate;
	}
}
