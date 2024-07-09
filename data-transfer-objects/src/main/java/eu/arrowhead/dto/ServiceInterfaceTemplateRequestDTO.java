package eu.arrowhead.dto;

import java.util.List;

public record ServiceInterfaceTemplateRequestDTO(
		String name,
		String protocol,
		List<ServiceInterfaceTemplatePropertyDTO> propertyRequirements) {

}
