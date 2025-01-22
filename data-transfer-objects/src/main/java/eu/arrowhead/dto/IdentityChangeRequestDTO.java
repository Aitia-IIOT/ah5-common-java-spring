package eu.arrowhead.dto;

import java.util.Map;

public record IdentityChangeRequestDTO(
		String systemName,
		Map<String, String> credentials,
		Map<String, String> newCredentials) implements ISystemName {
}