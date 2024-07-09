package eu.arrowhead.dto;

import java.util.List;

public record ServiceInstanceQueryRequestDTO(
		PageDTO pagination,
		List<String> instanceIds,
		List<String> providerNames,
		List<String> serviceDefinitionNames,
		List<String> versions,
		String alivesAt,
		List<MetadataRequirementDTO> metadataRequirementsList,
		List<String> interfaceTemplateNames,
		List<String> policies) {

}
