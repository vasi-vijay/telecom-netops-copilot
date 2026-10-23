
package com.telecom.netops.events;

import java.time.Instant;

public record TelecomKpiEvent(
        String eventId,
        int schemaVersion,
        String eventType,
        String operatorId,
        String towerId,
        Instant eventTime,
        long sequenceNumber,
        KpiMetrics metrics
) {

    public record KpiMetrics(
            double signalStrengthDbm,
            double rsrpDbm,
            double latencyMs,
            double packetLossPercent,
            int activeUsers,
            double cpuUtilizationPercent
    ) {}
}
