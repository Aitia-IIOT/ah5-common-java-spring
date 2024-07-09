package eu.arrowhead.dto;

public record LogEntryDTO(
		String logId,
		String entryDate,
		String logger,
		String severity,
		String message,
		String exception) {

}
