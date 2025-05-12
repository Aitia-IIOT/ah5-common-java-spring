package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
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
