package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationLookupRequestDTO(
		List<String> instanceIds,
		List<String> targets,
		List<String> clouds,
		String targetType) {
}