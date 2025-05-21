package eu.arrowhead.dto;

public record AuthorizationTokenGenerationMgmtRequestDTO(
		String tokenType,
		String targetType,
		String consumerCloud,
		String consumer,
		String provider,
		String target,
		String scope,
		String expireAt,
		Integer usageLimit) {
}