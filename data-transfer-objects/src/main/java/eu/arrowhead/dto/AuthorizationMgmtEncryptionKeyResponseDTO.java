package eu.arrowhead.dto;

public record AuthorizationMgmtEncryptionKeyResponseDTO(
		String systemName,
		String rawKey,
		String algorithm,
		String auxiliary,
		String createdAt) {

}
