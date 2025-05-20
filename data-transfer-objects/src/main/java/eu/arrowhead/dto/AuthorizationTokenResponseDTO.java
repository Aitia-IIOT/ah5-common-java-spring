package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationTokenType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationTokenResponseDTO(
		AuthorizationTokenType tokenType,
		String variant,
		String token,
		String tokenReference,
		String requester,
		String consumerCloud,
		String consumer,
		String provider,
		String serviceDefinition,
		String serviceOperation,
		String createdAt,
		Integer usageLimit,
		Integer usageLeft,
		String expiresAt) {
}
