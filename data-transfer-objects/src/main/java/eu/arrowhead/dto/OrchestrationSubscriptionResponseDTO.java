package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record OrchestrationSubscriptionResponseDTO(
		String id,
		String ownerSystemName,
		String targetSystemName,
		OrchestrationRequestDTO orchestrationRequest,
		OrchestrationNotifyInterfaceDTO notifyInterface,
		String expiredAt,
		String createdAt) {

}
