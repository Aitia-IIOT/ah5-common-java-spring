package eu.arrowhead.dto;

import java.util.List;

public record ServiceInstanceLookupRequestDTO(
		List<String> instanceIds,
		List<String> providerNames,
		List<String> serviceDefinitionNames,
		List<String> versions,
		String alivesAt,
		List<MetadataRequirementDTO> metadataRequirementsList,
		List<String> interfaceTemplateNames,
		List<MetadataRequirementDTO> interfacePropertyRequirementsList,
		List<String> policies) {

}
