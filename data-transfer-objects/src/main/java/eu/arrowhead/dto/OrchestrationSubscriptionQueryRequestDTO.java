package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationSubscriptionQueryRequestDTO(
		PageDTO pagination,
		List<String> ownerSystems,
		List<String> targetSystems,
		List<String> serviceDefinitions) {

}
