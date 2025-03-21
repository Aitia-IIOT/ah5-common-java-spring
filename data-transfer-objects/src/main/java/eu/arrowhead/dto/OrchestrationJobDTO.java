package eu.arrowhead.dto;

public record OrchestrationJobDTO(
		String id,
		String status,
		String type,
		String requesterSystem,
		String targetSystem,
		String serviceDefinition,
		String subscriptionId,
		String message,
		String createdAt,
		String startedAt,
		String finishedAt) {

}
