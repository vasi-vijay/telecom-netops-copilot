
package com.telecom.netops.validation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.telecom.netops.events.TelecomKpiEvent;

@Service
public class TelecomKpiEventValidator {

	public List<String> validate(TelecomKpiEvent event) {
		List<String> errors = new ArrayList<>();

		if (event == null) {
			errors.add("Event must not be null");
			return errors;
		}

		if (isBlank(event.eventId())) {
			errors.add("eventId is required");
		}

		if (event.schemaVersion() != 1) {
			errors.add("Unsupported schemaVersion: " + event.schemaVersion());
		}

		if (!"KPI_SNAPSHOT".equals(event.eventType())) {
			errors.add("Unsupported eventType: " + event.eventType());
		}

		if (isBlank(event.operatorId())) {
			errors.add("operatorId is required");
		}

		if (isBlank(event.towerId())) {
			errors.add("towerId is required");
		}

		if (event.eventTime() == null) {
			errors.add("eventTime is required");
		} else if (event.eventTime().isAfter(Instant.now())) {
			errors.add("eventTime must not be in the future");
		}

		if (event.sequenceNumber() <= 0) {
			errors.add("sequenceNumber must be positive");
		}

		TelecomKpiEvent.KpiMetrics metrics = event.metrics();

		if (metrics == null) {
			errors.add("metrics are required");
			return errors;
		}

		validateRange(metrics.signalStrengthDbm(), -140, 0, "signalStrengthDbm", errors);
		validateRange(metrics.rsrpDbm(), -140, -40, "rsrpDbm", errors);
		validateRange(metrics.latencyMs(), 0, Double.MAX_VALUE, "latencyMs", errors);
		validateRange(metrics.packetLossPercent(), 0, 100, "packetLossPercent", errors);

		if (metrics.activeUsers() < 0) {
			errors.add("activeUsers must not be negative");
		}

		validateRange(metrics.cpuUtilizationPercent(), 0, 100, "cpuUtilizationPercent", errors);

		return errors;
	}

	private void validateRange(double value, double min, double max, String field, List<String> errors) {

		if (!Double.isFinite(value) || value < min || value > max) {
			errors.add(field + " must be between " + min + " and " + max);
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
