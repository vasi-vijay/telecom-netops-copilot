CREATE TABLE IF NOT EXISTS telecom_kpi_events (
    event_id VARCHAR(100) PRIMARY KEY,
    schema_version INTEGER NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    operator_id VARCHAR(50) NOT NULL,
    tower_id VARCHAR(100) NOT NULL,
    event_time TIMESTAMPTZ NOT NULL,
    sequence_number BIGINT NOT NULL CHECK (sequence_number > 0),

    signal_strength_dbm DOUBLE PRECISION NOT NULL,
    rsrp_dbm DOUBLE PRECISION NOT NULL,
    latency_ms DOUBLE PRECISION NOT NULL,
    packet_loss_percent DOUBLE PRECISION NOT NULL,
    active_users INTEGER NOT NULL,
    cpu_utilization_percent DOUBLE PRECISION NOT NULL,

    received_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_signal_strength
        CHECK (signal_strength_dbm BETWEEN -140 AND 0),

    CONSTRAINT chk_rsrp
        CHECK (rsrp_dbm BETWEEN -140 AND -40),

    CONSTRAINT chk_latency
        CHECK (latency_ms >= 0),

    CONSTRAINT chk_packet_loss
        CHECK (packet_loss_percent BETWEEN 0 AND 100),

    CONSTRAINT chk_active_users
        CHECK (active_users >= 0),

    CONSTRAINT chk_cpu_utilization
        CHECK (cpu_utilization_percent BETWEEN 0 AND 100)
);

CREATE INDEX IF NOT EXISTS idx_telecom_kpi_operator_tower_time
    ON telecom_kpi_events (operator_id, tower_id, event_time DESC);
