package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationHistoryQueryRequestDTO(
		PageDTO pagination,
		List<String> ids,
		List<String> statuses,
		String type,
		List<String> requesterSystrems,
		List<String> targetSystems,
		List<String> serviceDefinitions,
		List<String> subscriptionIds) {

}
