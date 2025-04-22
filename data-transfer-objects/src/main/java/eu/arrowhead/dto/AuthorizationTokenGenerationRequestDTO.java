package eu.arrowhead.dto;

public record AuthorizationTokenGenerationRequestDTO(
		String tokenType,
		String serviceInstanceId,
		String serviceOperation) {
}