
package com.telecom.netops.ingestion;

import java.time.Instant;

import com.telecom.netops.events.TelecomKpiEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "telecom_kpi_events")
public class TelecomKpiEventEntity {

	@Id
	@Column(name = "event_id", nullable = false, length = 100)
	private String eventId;

	@Column(name = "schema_version", nullable = false)
	private int schemaVersion;

	@Column(name = "event_type", nullable = false, length = 50)
	private String eventType;

	@Column(name = "operator_id", nullable = false, length = 50)
	private String operatorId;

	@Column(name = "tower_id", nullable = false, length = 100)
	private String towerId;

	@Column(name = "event_time", nullable = false)
	private Instant eventTime;

	@Column(name = "sequence_number", nullable = false)
	private long sequenceNumber;

	@Column(name = "signal_strength_dbm", nullable = false)
	private double signalStrengthDbm;

	@Column(name = "rsrp_dbm", nullable = false)
	private double rsrpDbm;

	@Column(name = "latency_ms", nullable = false)
	private double latencyMs;

	@Column(name = "packet_loss_percent", nullable = false)
	private double packetLossPercent;

	@Column(name = "active_users", nullable = false)
	private int activeUsers;

	@Column(name = "cpu_utilization_percent", nullable = false)
	private double cpuUtilizationPercent;

	@Column(name = "received_at", nullable = false, insertable = false, updatable = false)
	private Instant receivedAt;

	protected TelecomKpiEventEntity() {
		// Required by JPA
	}

	public static TelecomKpiEventEntity from(TelecomKpiEvent event) {
		TelecomKpiEventEntity entity = new TelecomKpiEventEntity();

		entity.eventId = event.eventId();
		entity.schemaVersion = event.schemaVersion();
		entity.eventType = event.eventType();
		entity.operatorId = event.operatorId();
		entity.towerId = event.towerId();
		entity.eventTime = event.eventTime();
		entity.sequenceNumber = event.sequenceNumber();

		TelecomKpiEvent.KpiMetrics metrics = event.metrics();

		entity.signalStrengthDbm = metrics.signalStrengthDbm();
		entity.rsrpDbm = metrics.rsrpDbm();
		entity.latencyMs = metrics.latencyMs();
		entity.packetLossPercent = metrics.packetLossPercent();
		entity.activeUsers = metrics.activeUsers();
		entity.cpuUtilizationPercent = metrics.cpuUtilizationPercent();

		return entity;
	}

	@PrePersist
	private void validateRequiredFields() {
		if (eventId == null || eventTime == null) {
			throw new IllegalStateException("Event ID and event time are required");
		}
	}

	public String getEventId() {
		return eventId;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public int getSchemaVersion() {
		return schemaVersion;
	}

	public String getEventType() {
		return eventType;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public double getSignalStrengthDbm() {
		return signalStrengthDbm;
	}

	public double getRsrpDbm() {
		return rsrpDbm;
	}

	public double getLatencyMs() {
		return latencyMs;
	}

	public double getPacketLossPercent() {
		return packetLossPercent;
	}

	public int getActiveUsers() {
		return activeUsers;
	}

	public double getCpuUtilizationPercent() {
		return cpuUtilizationPercent;
	}

	public String getTowerId() {
		return towerId;
	}

	public Instant getEventTime() {
		return eventTime;
	}

	public Instant getReceivedAt() {
		return receivedAt;
	}
}
