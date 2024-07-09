package eu.arrowhead.dto;

import java.util.List;

public record ServiceInterfaceTemplateResponseDTO(
		String name,
		String protocol,
		List<ServiceInterfaceTemplatePropertyResponseDTO> propertyRequirements,
		String createdAt,
		String updatedAt) {

}
