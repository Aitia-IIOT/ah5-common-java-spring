package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record LogEntryDTO(
		String logId,
		String entryDate,
		String logger,
		String severity,
		String message,
		String exception) {
}