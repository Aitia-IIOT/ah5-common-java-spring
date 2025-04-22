package eu.arrowhead.dto;

public record AuthorizationTokenQueryRequestDTO(
		PageDTO pagination,
		String requester,
		String tokenType,
		String consumerCloud,
		String consumer,
		String provider,
		String serviceDefinition) {
}