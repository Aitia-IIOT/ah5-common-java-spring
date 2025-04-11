package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationLookupRequestDTO(
		List<String> instanceIds,
		List<String> cloudIdentifiers,
		List<String> targetNames,
		String targetType) {
}