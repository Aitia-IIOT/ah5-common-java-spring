package eu.arrowhead.dto;

public record AuthorizationTokenGenerationMgmtRequestDTO(
		String tokenType,
		String consumerCloud,
		String consumer,
		String provider,
		String serviceDefinition,
		String serviceOperation,
		String expireAt,
		Integer usageLimit) {
}