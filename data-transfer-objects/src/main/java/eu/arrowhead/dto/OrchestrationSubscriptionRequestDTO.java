package eu.arrowhead.dto;

public record OrchestrationSubscriptionRequestDTO(
		String targetSystemName,
		OrchestrationRequestDTO orchestrationRequest,
		OrchestrationNotifyInterfaceDTO notifyInterface,
		Long duration) {

}
