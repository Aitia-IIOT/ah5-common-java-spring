package eu.arrowhead.dto;

public record IdentitySessionQueryRequestDTO(
		PageDTO pagination,
		String namePart,
		String loginFrom,
		String loginTo) {
}