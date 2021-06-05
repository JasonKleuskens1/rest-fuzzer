package nl.ou.se.rest.fuzzer.components.service.util;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseDto {

	// variable(s)
	private List<String> violations = new ArrayList<>();

	// constructor(s)
	public HttpResponseDto(List<String> violations) {
		this.violations = violations;
	}

	public HttpResponseDto(String violation) {
		this.violations.add(violation);
	}

	// getters and setters
	public List<String> getViolations() {
		return violations;
	}

	public void setViolations(List<String> violations) {
		this.violations = violations;
	}	
}
