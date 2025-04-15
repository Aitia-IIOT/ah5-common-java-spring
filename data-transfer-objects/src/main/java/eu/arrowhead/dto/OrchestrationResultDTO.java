package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record OrchestrationResultDTO(
		String serviceInstanceId,
		String cloudIdentitifer,
		String providerName,
		String serviceDefinitition,
		String version,
		String aliveUntil,
		String exclusiveUntil,
		Map<String, Object> metadata,
		List<ServiceInstanceInterfaceResponseDTO> interfaces) {

}
