package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationSimpleStoreQueryRequestDTO(
		PageDTO pagination,
		List<String> ids,
		List<String> consumerNames,
		List<String> serviceDefinitionNames,
		List<String> serviceInstanceIds,
		Integer minPriority,
		Integer maxPriority,
		String createdBy) {

}
