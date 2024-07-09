package eu.arrowhead.dto;

public record PageDTO(
		Integer page,
		Integer size,
		String direction,
		String sortField) {
}
