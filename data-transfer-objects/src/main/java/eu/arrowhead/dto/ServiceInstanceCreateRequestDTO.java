package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record ServiceInstanceCreateRequestDTO(
		String serviceDefinitionName,
		String version,
		String expiresAt,
		Map<String, Object> metadata,
		List<ServiceInstanceInterfaceRequestDTO> interfaces) {
}