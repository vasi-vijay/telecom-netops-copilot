package com.telecom.netops.ingestion;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class KpiEventPersistenceRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public int insertIfAbsent(TelecomKpiEventEntity event) {
		return entityManager.createNativeQuery("""
				INSERT INTO telecom_kpi_events (
				    event_id,
				    schema_version,
				    event_type,
				    operator_id,
				    tower_id,
				    event_time,
				    sequence_number,
				    signal_strength_dbm,
				    rsrp_dbm,
				    latency_ms,
				    packet_loss_percent,
				    active_users,
				    cpu_utilization_percent
				)
				VALUES (
				    :eventId,
				    :schemaVersion,
				    :eventType,
				    :operatorId,
				    :towerId,
				    :eventTime,
				    :sequenceNumber,
				    :signalStrength,
				    :rsrp,
				    :latency,
				    :packetLoss,
				    :activeUsers,
				    :cpuUtilization
				)
				ON CONFLICT (event_id) DO NOTHING
				""").setParameter("eventId", event.getEventId()).setParameter("schemaVersion", event.getSchemaVersion())
				.setParameter("eventType", event.getEventType()).setParameter("operatorId", event.getOperatorId())
				.setParameter("towerId", event.getTowerId()).setParameter("eventTime", event.getEventTime())
				.setParameter("sequenceNumber", event.getSequenceNumber())
				.setParameter("signalStrength", event.getSignalStrengthDbm()).setParameter("rsrp", event.getRsrpDbm())
				.setParameter("latency", event.getLatencyMs()).setParameter("packetLoss", event.getPacketLossPercent())
				.setParameter("activeUsers", event.getActiveUsers())
				.setParameter("cpuUtilization", event.getCpuUtilizationPercent()).executeUpdate();
	}
}
