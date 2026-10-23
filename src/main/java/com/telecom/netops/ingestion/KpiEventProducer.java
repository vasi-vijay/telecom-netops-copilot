
package com.telecom.netops.ingestion;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.telecom.netops.events.TelecomKpiEvent;

@Service
public class KpiEventProducer {

	private static final String TOPIC = "telecom.kpi.events";

	private final KafkaTemplate<String, TelecomKpiEvent> kafkaTemplate;

	public KpiEventProducer(KafkaTemplate<String, TelecomKpiEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publish(TelecomKpiEvent event) {
		kafkaTemplate.send(TOPIC, event.towerId(), event);
	}
}
