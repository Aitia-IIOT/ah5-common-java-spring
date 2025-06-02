package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationTargetType;
import eu.arrowhead.dto.enums.AuthorizationTokenType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationTokenResponseDTO(
		AuthorizationTokenType tokenType,
		String variant,
		String token, // Raw or encrypted token
		String tokenReference, // Hashed token (as stored in DB)
		String requester,
		String consumerCloud,
		String consumer,
		String provider,
		AuthorizationTargetType targetType,
		String target,
		String scope,
		String createdAt,
		Integer usageLimit,
		Integer usageLeft,
		String expiresAt) {
}
