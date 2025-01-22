package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationServiceRequirementDTO(
		String serviceDefinition,
		List<String> operations,
		List<String> versions,
		String alivesAt,
		List<MetadataRequirementDTO> metadataRequirements,
		List<String> interfaceTemplateNames,
		List<String> interfaceAddressTypes,
		List<MetadataRequirementDTO> interfacePropertyRequirements,
		List<String> securityPolicies,
		List<String> prefferedProviders) {

	// TODO
}
