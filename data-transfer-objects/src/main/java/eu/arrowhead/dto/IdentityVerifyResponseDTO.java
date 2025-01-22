package eu.arrowhead.dto;

public record IdentityVerifyResponseDTO(
		boolean verified,
		String systemName,
		Boolean sysop,
		String loginTime,
		String expirationTime) {
}