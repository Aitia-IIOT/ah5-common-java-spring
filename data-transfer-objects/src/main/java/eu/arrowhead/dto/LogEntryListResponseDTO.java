package eu.arrowhead.dto;

import java.util.List;

public record LogEntryListResponseDTO(
		List<LogEntryDTO> entries,
		long count) {

}
