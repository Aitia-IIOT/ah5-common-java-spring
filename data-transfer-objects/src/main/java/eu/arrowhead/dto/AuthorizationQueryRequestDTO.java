package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationQueryRequestDTO(
		PageDTO pagination,
		String level,
		List<String> providers,
		List<String> instanceIds,
		List<String> cloudIdentifiers,
		List<String> targetNames,
		String targetType) {
}