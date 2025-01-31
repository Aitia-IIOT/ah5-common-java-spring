package eu.arrowhead.dto;

import java.util.Map;

public record ServiceInstanceInterfaceRequestDTO(
		String templateName,
		String protocol,
		String policy,
		Map<String, Object> properties) {
}