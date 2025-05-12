package eu.arrowhead.dto;

public record OrchestrationSimpleStoreResponseDTO(
		String id,
		String consumer,
		String serviceDefinition,
		String serviceInstanceId,
		Integer Priority,
		String createdBy,
		String updatedBy,
		String createdAt,
		String updatedAt) {

}
