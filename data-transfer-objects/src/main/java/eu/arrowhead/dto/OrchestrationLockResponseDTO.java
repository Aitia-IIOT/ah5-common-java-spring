package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record OrchestrationLockResponseDTO(
		long id,
		String orchestrationJobId,
		String serviceInstanceId,
		String owner,
		String expiresAt,
		boolean temporary) {

}
