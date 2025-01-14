package eu.arrowhead.dto;

import java.util.List;

public record ServiceInterfaceTemplateQueryRequestDTO(
		PageDTO pagination,
		List<String> templateNames,
		List<String> protocols) {
}