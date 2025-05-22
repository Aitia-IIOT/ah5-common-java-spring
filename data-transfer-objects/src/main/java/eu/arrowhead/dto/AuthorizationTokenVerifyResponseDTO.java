package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationTargetType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationTokenVerifyResponseDTO(
		boolean verified,
		String consumerCloud,
		String consumer,
		AuthorizationTargetType targetType,
		String target,
		String scope) {
}