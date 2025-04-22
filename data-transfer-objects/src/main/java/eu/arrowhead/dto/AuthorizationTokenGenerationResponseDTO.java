package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationTokenType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationTokenGenerationResponseDTO(
		AuthorizationTokenType tokenType,
		String token,
		Integer usageLimit,
		String expiresAt) {
}
