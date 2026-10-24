package com.telecom.netops.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TelecomKpiEventJpaRepository extends JpaRepository<TelecomKpiEventEntity, String> {
}
