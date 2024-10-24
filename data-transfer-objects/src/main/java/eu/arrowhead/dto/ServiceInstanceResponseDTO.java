package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record ServiceInstanceResponseDTO(
		String instanceId,
		SystemResponseDTO provider,
		ServiceDefinitionResponseDTO serviceDefinition,
		String version,
		String expiresAt,
		Map<String, Object> metadata,
		List<ServiceInstanceInterfaceResponseDTO> interfaces,
		String createdAt,
		String updatedAt) {

}
