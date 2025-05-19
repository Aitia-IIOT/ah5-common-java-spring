package eu.arrowhead.dto;

public record AuthorizationMgmtEncryptionKeyResponseDTO(
		String systemName,
		String keyValue,
		String algorithm,
		String auxiliary,
		String createdAt) {

}
