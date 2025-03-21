package eu.arrowhead.dto;

public record OrchestrationLockRequestDTO(
		String serviceInstanceId,
		String owner,
		String expiresAt) {

}
