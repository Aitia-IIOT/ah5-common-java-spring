package eu.arrowhead.dto;

public record IdentityLoginResponseDTO(
		String token,
		String expirationTime) {
}