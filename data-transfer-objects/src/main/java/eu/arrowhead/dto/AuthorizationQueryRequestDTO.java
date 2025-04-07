package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationQueryRequestDTO(
		PageDTO pagination,
		String level,
		List<String> instanceIds,
		List<String> targets,
		List<String> clouds,
		String targetType) {
}