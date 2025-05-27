package eu.arrowhead.dto;

public record AuthorizationMgmtEncryptionKeyResponseDTO(
		String systemName,
		String rawKey,
		String algorithm,
		String keyAdditive,
		String createdAt) {

}
