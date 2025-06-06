package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record AuthorizationMgmtEncryptionKeyResponseDTO(
		String systemName,
		String rawKey,
		String algorithm,
		String keyAdditive,
		String createdAt) {
}