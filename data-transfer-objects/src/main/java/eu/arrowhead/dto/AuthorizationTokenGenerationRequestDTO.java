package eu.arrowhead.dto;

public record AuthorizationTokenGenerationRequestDTO(
		String tokenVariant,
		String provider,
		String targetType,
		String target,
		String scope) {
}