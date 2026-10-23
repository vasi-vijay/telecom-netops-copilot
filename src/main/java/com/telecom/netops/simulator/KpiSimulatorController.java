
package com.telecom.netops.simulator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.netops.events.TelecomKpiEvent;
import com.telecom.netops.ingestion.KpiEventProducer;

@RestController
@RequestMapping("/api/v1/simulator")
public class KpiSimulatorController {

	private final KpiSimulatorService simulatorService;
	private final KpiEventProducer eventProducer;

	public KpiSimulatorController(KpiSimulatorService simulatorService, KpiEventProducer eventProducer) {
		this.simulatorService = simulatorService;
		this.eventProducer = eventProducer;
	}

	@GetMapping("/event")
	public TelecomKpiEvent generateEvent(@RequestParam(defaultValue = "VIL") String operatorId,
			@RequestParam(defaultValue = "TOWER-001") String towerId,
			@RequestParam(defaultValue = "normal") String scenario) {
		TelecomKpiEvent event = simulatorService.generateEvent(operatorId, towerId, scenario);
		eventProducer.publish(event);
		return event;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleInvalidScenario(IllegalArgumentException exception) {
		return exception.getMessage();
	}
}
