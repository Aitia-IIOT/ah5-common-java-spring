package eu.arrowhead.dto;

import java.util.List;

public record ServiceInterfaceTemplateListResponseDTO(
		List<ServiceInterfaceTemplateResponseDTO> entries,
		long count) {

}
