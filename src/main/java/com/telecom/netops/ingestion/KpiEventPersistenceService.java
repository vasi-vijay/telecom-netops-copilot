package com.telecom.netops.ingestion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.netops.events.TelecomKpiEvent;

@Service
public class KpiEventPersistenceService {

	private final KpiEventPersistenceRepository persistenceRepository;

	public KpiEventPersistenceService(KpiEventPersistenceRepository persistenceRepository) {
		this.persistenceRepository = persistenceRepository;
	}

	@Transactional
	public PersistenceResult persistIfAbsent(TelecomKpiEvent event) {
		TelecomKpiEventEntity entity = TelecomKpiEventEntity.from(event);

		int inserted = persistenceRepository.insertIfAbsent(entity);

		return inserted == 1 ? PersistenceResult.INSERTED : PersistenceResult.DUPLICATE;
	}

	public enum PersistenceResult {
		INSERTED, DUPLICATE
	}
}
