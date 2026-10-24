package com.telecom.netops.ingestion;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.telecom.netops.events.TelecomKpiEvent;
import com.telecom.netops.validation.TelecomKpiEventValidator;

@Component
public class KpiEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(KpiEventConsumer.class);

	private final TelecomKpiEventValidator validator;
	private final KpiEventPersistenceService persistenceService;

	public KpiEventConsumer(TelecomKpiEventValidator validator, KpiEventPersistenceService persistenceService) {
		this.validator = validator;
		this.persistenceService = persistenceService;
	}

	@KafkaListener(topics = "telecom.kpi.events")
	public void consume(TelecomKpiEvent event) {
		List<String> errors = validator.validate(event);
		if (!errors.isEmpty()) {
			log.warn("Invalid KPI event rejected. eventId={}, errors={}", event.eventId(), errors);
			throw new IllegalArgumentException("KPI event validation failed: " + errors);
		}
		KpiEventPersistenceService.PersistenceResult result = persistenceService.persistIfAbsent(event);
		switch (result) {
		case INSERTED -> log.info("KPI event persisted. eventId={}, operatorId={}, towerId={}", event.eventId(),
				event.operatorId(), event.towerId());
		case DUPLICATE -> log.info("Duplicate KPI event ignored. eventId={}", event.eventId());
		}
	}

}
