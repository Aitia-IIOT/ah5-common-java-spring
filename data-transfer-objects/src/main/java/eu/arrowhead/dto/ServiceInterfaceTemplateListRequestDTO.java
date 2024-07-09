package eu.arrowhead.dto;

import java.util.List;

public record ServiceInterfaceTemplateListRequestDTO(
		List<ServiceInterfaceTemplateRequestDTO> interfaceTemplates) {

}
