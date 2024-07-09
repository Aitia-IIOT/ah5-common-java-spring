package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record ServiceInstanceUpdateRequestDTO(
		String instanceId,
		String expiresAt,
		Map<String, Object> metadata,
		List<ServiceInstanceInterfaceRequestDTO> interfaces) {

}
