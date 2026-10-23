
package com.telecom.netops.ingestion;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.telecom.netops.events.TelecomKpiEvent;
import com.telecom.netops.validation.TelecomKpiEventValidator;

@Service
public class KpiEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(KpiEventConsumer.class);

	private final TelecomKpiEventValidator validator;

	public KpiEventConsumer(TelecomKpiEventValidator validator) {
		this.validator = validator;
	}

	@KafkaListener(topics = "telecom.kpi.events")
	public void consume(TelecomKpiEvent event) {
		List<String> errors = validator.validate(event);

		if (!errors.isEmpty()) {
			log.warn("Rejected KPI event. eventId={}, errors={}", event.eventId(), errors);
			return;
		}

		log.info("KPI event validated. eventId={}, operatorId={}, towerId={}, sequenceNumber={}", event.eventId(),
				event.operatorId(), event.towerId(), event.sequenceNumber());
	}
}
