package eu.arrowhead.dto;

import java.util.Map;

public record IdentityRequestDTO(
		String systemName,
		Map<String, String> credentials) implements ISystemName {
}