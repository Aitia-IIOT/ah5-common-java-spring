package eu.arrowhead.dto;

public record OrchestrationSimpleStoreRequestDTO(
		String consumer,
		String serviceDefinition,
		String serviceInstanceId,
		Integer priority) {
	
}
