package eu.arrowhead.dto;

public record AuthorizationVerifyRequestDTO(
		String provider,
		String consumer,
		String cloud,
		String targetType,
		String target,
		String scope) {
}