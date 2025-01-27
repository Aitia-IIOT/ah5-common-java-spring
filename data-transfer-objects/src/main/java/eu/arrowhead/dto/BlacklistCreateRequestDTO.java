package eu.arrowhead.dto;

public record BlacklistCreateRequestDTO(
		String systemName,
		String expiresAt,
		String reason) {

}
