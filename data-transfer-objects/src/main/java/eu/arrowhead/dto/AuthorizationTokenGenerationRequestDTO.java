package eu.arrowhead.dto;

public record AuthorizationTokenGenerationRequestDTO(
		String tokenType,
		String provider,
		String targetType,
		String target,
		String scope) {
}