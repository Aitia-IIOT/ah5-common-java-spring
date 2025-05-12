package eu.arrowhead.dto;

public record OrchestrationSimpleStoreRequestDTO(
		String consumer,
		String serviceInstanceId,
		Integer priority) {
}
