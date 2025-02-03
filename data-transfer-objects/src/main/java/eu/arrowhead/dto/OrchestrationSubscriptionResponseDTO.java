package eu.arrowhead.dto;

public record OrchestrationSubscriptionResponseDTO(
		String id,
		String ownerSystemName,
		String targetSystemName,
		OrchestrationRequestDTO orchestrationRequest,
		OrchestrationNotifyInterfaceDTO notifyInterface,
		Long duration,
		String createdAt) {

}
