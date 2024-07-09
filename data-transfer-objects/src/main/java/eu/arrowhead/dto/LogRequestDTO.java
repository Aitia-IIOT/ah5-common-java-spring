package eu.arrowhead.dto;

public record LogRequestDTO(
		PageDTO pagination,
		String from,
		String to,
		String severity,
		String logger) {

}
