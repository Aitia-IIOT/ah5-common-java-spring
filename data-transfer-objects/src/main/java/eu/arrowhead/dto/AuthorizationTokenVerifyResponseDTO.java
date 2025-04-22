package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record AuthorizationTokenVerifyResponseDTO(
		boolean verified,
		String consumerCloud,
		String consumer,
		String serviceDefinition,
		String serviceOperation) {
}