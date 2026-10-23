
package com.telecom.netops.validation;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.netops.events.TelecomKpiEvent;

@RestController
@RequestMapping("/api/v1/validation")
public class EventValidationController {

	private final TelecomKpiEventValidator validator;

	public EventValidationController(TelecomKpiEventValidator validator) {
		this.validator = validator;
	}

	@PostMapping("/kpi-event")
	public ValidationResponse validate(@RequestBody TelecomKpiEvent event) {

		List<String> errors = validator.validate(event);

		return new ValidationResponse(errors.isEmpty(), errors);
	}

	public record ValidationResponse(boolean valid, List<String> errors) {
	}
}
