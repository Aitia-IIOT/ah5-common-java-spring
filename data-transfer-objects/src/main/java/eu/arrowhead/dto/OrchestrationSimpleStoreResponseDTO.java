package eu.arrowhead.dto;

public record OrchestrationSimpleStoreResponseDTO(
		String id,
		String consumer,
		String serviceDefinition,
		String serviceInstanceId,
		String createdBy,
		String updatedBy,
		String createdAt,
		String updatedAt) {

}
