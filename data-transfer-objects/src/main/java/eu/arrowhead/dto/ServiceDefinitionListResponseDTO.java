package eu.arrowhead.dto;

import java.util.List;

public record ServiceDefinitionListResponseDTO(
		List<ServiceDefinitionResponseDTO> entries,
		long count) {

}
