package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record ServiceDefinitionResponseDTO(
		String name,
		String createdAt,
		String updatedAt) {

}
