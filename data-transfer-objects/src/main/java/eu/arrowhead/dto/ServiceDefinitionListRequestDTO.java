package eu.arrowhead.dto;

import java.util.List;

public record ServiceDefinitionListRequestDTO(
		List<String> serviceDefinitionNames) {

}
