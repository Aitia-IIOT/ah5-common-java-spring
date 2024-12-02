package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record SystemRequestDTO(
		String name,
		Map<String, Object> metadata,
		String version,
		List<String> addresses,
		String deviceName) {
}