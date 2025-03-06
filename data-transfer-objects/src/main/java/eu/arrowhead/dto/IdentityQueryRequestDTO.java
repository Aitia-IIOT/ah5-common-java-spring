package eu.arrowhead.dto;

public record IdentityQueryRequestDTO(
		PageDTO pagination,
		String namePart,
		Boolean isSysop,
		String createdBy,
		String creationFrom,
		String creationTo,
		Boolean hasSession) {
}