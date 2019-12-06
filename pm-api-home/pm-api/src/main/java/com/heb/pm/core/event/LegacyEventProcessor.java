package com.heb.pm.core.event;

import com.heb.pm.core.repository.LegacyEventRepository;
import com.heb.pm.dao.core.entity.LegacyEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.*;

/**
 * Service to handle staging legacy events.
 *
 * @author d116773
 * @since 1.8.0
 */
@Service
public class LegacyEventProcessor implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(LegacyEventProcessor.class);

	@Value("${app.eventProcessor.flushSize:100}")
	private transient int flushSize;

	@Value("${app.eventProcessor.cycleTime:300000}")
	private transient long cycleTime;

	private final transient List<LegacyEvent> events = new LinkedList<>();

	private transient Timer timer;

	@Autowired
	private transient LegacyEventRepository legacyEventRepository;

	/**
	 * Called by Spring after all properties have been set.
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		logger.info(String.format("Setting up legacy event publisher with a cycle time of %d.", this.cycleTime));

		// Set up a time to stage events that are just hanging out.
		this.timer = new Timer("Legacy Event Publication");
		this.timer.scheduleAtFixedRate(new EventProcessorTimer(this), this.cycleTime, this.cycleTime);
	}


	/**
	 * TimerTask that will trigger staging events that are lingering.
	 */
	@AllArgsConstructor
	private static class EventProcessorTimer extends TimerTask {

		private final LegacyEventProcessor legacyEventProcessor;

		@Override
		public void run() {
			this.legacyEventProcessor.flush();
		}
	}

	/**
	 * Called just before the system shuts down.
	 */
	@PreDestroy
	public void preDestroy() {

		this.timer.cancel();

		logger.info("Flushing queue before shutdown.");
		this.flush();
	}

	/**
	 * Adds a bunch of events to stage.
	 *
	 * @param eventsToAdd A collection of events to stage.
	 */
	public void add(Collection<? extends LegacyEvent> eventsToAdd) {

		synchronized (this.events) {
			this.events.addAll(eventsToAdd);
		}
		this.processIfFull();
	}

	/**
	 * Adds a single event to stage.
	 *
	 * @param eventToAdd The event to stage.
	 */
	public void add(LegacyEvent eventToAdd) {

		synchronized (this.events) {
			this.events.add(eventToAdd);
		}
		this.processIfFull();
	}

	/**
	 * Sees if the list of events is greater than the flush size and flushes them if so.
	 */
	protected void processIfFull() {

		if (this.events.size() > this.flushSize) {
			this.flush();
		}
	}

	/**
	 * Extracts the events from the list and then flushes them.
	 */
	@Transactional
	public void flush() {

		logger.info("Beginning staging legacy events.");

		List<LegacyEvent> eventsToStage;

		synchronized (this.events) {
			eventsToStage = new LinkedList<>(this.events);
			this.events.clear();
		}

		if (!eventsToStage.isEmpty()) {
			this.stageEvents(eventsToStage);
		}

		logger.info(String.format("Staged %d legacy events.", eventsToStage.size()));
	}


	/**
	 * Handles the actual save of the events to the table.
	 *
	 * @param eventsToStage The events to save.
	 */
	@Transactional
	protected void stageEvents(List<LegacyEvent> eventsToStage) {

		logger.debug(String.format("Staging chunk of %d events.", eventsToStage.size()));
		if (logger.isDebugEnabled()) {
			eventsToStage.forEach((i) -> logger.debug(i.toString()));
		}
		this.legacyEventRepository.saveAll(eventsToStage);
	}
}
