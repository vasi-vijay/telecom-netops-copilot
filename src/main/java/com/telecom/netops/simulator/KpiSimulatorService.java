
package com.telecom.netops.simulator;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.telecom.netops.events.TelecomKpiEvent;

@Service
public class KpiSimulatorService {

	private final AtomicLong sequence = new AtomicLong();

	public TelecomKpiEvent generateEvent(String operatorId, String towerId, String scenario) {

		ThreadLocalRandom random = ThreadLocalRandom.current();

		double signalStrength = random.nextDouble(-85, -55);
		double rsrp = random.nextDouble(-115, -75);
		double latency = random.nextDouble(10, 80);
		double packetLoss = random.nextDouble(0, 2);
		int activeUsers = random.nextInt(50, 500);
		double cpuUtilization = random.nextDouble(20, 75);

		switch (scenario.toLowerCase()) {
		case "congestion" -> {
			latency = random.nextDouble(100, 300);
			packetLoss = random.nextDouble(3, 12);
			activeUsers = random.nextInt(700, 1500);
			cpuUtilization = random.nextDouble(80, 99);
		}
		case "signal-degradation" -> {
			signalStrength = random.nextDouble(-115, -95);
			rsrp = random.nextDouble(-125, -115);
			latency = random.nextDouble(60, 180);
			packetLoss = random.nextDouble(1, 8);
		}
		case "normal" -> {
			// Keep the normal ranges generated above.
		}
		default -> throw new IllegalArgumentException("Unsupported scenario: " + scenario);
		}

		TelecomKpiEvent.KpiMetrics metrics = new TelecomKpiEvent.KpiMetrics(signalStrength, rsrp, latency, packetLoss,
				activeUsers, cpuUtilization);

		return new TelecomKpiEvent(UUID.randomUUID().toString(), 1, "KPI_SNAPSHOT", operatorId, towerId, Instant.now(),
				sequence.incrementAndGet(), metrics);
	}
}
