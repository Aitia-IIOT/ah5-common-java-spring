package eu.arrowhead.dto;

public record OrchestrationSimpleStoreResponseDTO(
		long id,
		String consumer,
		String serviceDefinition,
		String serviceInstanceId,
		String createdBy,
		String updatedBy,
		String createdAt,
		String updatedAt) {

}
