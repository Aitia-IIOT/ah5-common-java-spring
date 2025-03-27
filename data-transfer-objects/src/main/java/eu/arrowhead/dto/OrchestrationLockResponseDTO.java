package eu.arrowhead.dto;

public record OrchestrationLockResponseDTO(
		long id,
		String orchestrationJobId,
		String serviceInstanceId,
		String owner,
		String expiresAt,
		boolean temporary) {

}
