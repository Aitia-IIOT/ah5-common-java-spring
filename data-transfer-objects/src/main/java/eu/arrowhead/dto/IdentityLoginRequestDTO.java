package eu.arrowhead.dto;

import java.util.Map;

public record IdentityLoginRequestDTO(
		String systemName,
		Map<String, String> credentials) {
}