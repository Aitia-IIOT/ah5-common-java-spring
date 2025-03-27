package eu.arrowhead.dto;

public record IdentitySessionResponseDTO(
		String systemName,
		String loginTime,
		String expirationTime) {
}