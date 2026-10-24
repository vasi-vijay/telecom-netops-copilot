package com.telecom.netops.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.telecom.netops.events.TelecomKpiEvent;
import com.telecom.netops.netops_copilot.NetopsCopilotApplication;

@SpringBootTest(classes = NetopsCopilotApplication.class, properties = "spring.kafka.listener.auto-startup=false")
class KpiEventPersistenceServiceTest {

	@Autowired
	private KpiEventPersistenceService persistenceService;

	@Autowired
	private TelecomKpiEventJpaRepository eventRepository;

	private final Set<String> testEventIds = ConcurrentHashMap.newKeySet();

	@AfterEach
	void cleanUp() {
		testEventIds.forEach(eventRepository::deleteById);
	}

	@Test
	void shouldInsertEventOnlyOnceWhenDeliveredTwice() {
		TelecomKpiEvent event = createEvent();
		testEventIds.add(event.eventId());

		var firstResult = persistenceService.persistIfAbsent(event);
		var secondResult = persistenceService.persistIfAbsent(event);

		assertEquals(KpiEventPersistenceService.PersistenceResult.INSERTED, firstResult);

		assertEquals(KpiEventPersistenceService.PersistenceResult.DUPLICATE, secondResult);

		assertTrue(eventRepository.existsById(event.eventId()));
	}

	@Test
	void shouldInsertEventOnlyOnceForConcurrentDeliveries() throws Exception {

		TelecomKpiEvent event = createEvent();
		testEventIds.add(event.eventId());

		int workers = 10;

		ExecutorService executor = Executors.newFixedThreadPool(workers);

		CountDownLatch ready = new CountDownLatch(workers);
		CountDownLatch start = new CountDownLatch(1);

		try {
			List<CompletableFuture<KpiEventPersistenceService.PersistenceResult>> futures = new ArrayList<>();

			for (int i = 0; i < workers; i++) {
				futures.add(CompletableFuture.supplyAsync(() -> {
					ready.countDown();

					try {
						if (!start.await(10, TimeUnit.SECONDS)) {
							throw new IllegalStateException("Timed out waiting to start test");
						}
					} catch (InterruptedException exception) {
						Thread.currentThread().interrupt();
						throw new CompletionException(exception);
					}

					return persistenceService.persistIfAbsent(event);
				}, executor));
			}

			assertTrue(ready.await(10, TimeUnit.SECONDS), "All workers should be ready");

			start.countDown();

			long insertedCount = 0;
			long duplicateCount = 0;

			for (CompletableFuture<KpiEventPersistenceService.PersistenceResult> future : futures) {

				var result = future.join();

				if (result == KpiEventPersistenceService.PersistenceResult.INSERTED) {
					insertedCount++;
				} else {
					duplicateCount++;
				}
			}

			assertEquals(1, insertedCount);
			assertEquals(workers - 1, duplicateCount);
			assertTrue(eventRepository.existsById(event.eventId()));

		} finally {
			start.countDown();
			executor.shutdownNow();
		}
	}

	private TelecomKpiEvent createEvent() {
		return new TelecomKpiEvent(UUID.randomUUID().toString(), 1, "KPI_SNAPSHOT", "VIL", "TOWER-TEST-001",
				Instant.now(), 1L, new TelecomKpiEvent.KpiMetrics(-75.0, -95.0, 30.0, 0.5, 100, 45.0));
	}
}
