package eu.arrowhead.dto;

import java.util.List;

public record SystemListResponseDTO(
		List<SystemResponseDTO> entries,
		long count) {

}
