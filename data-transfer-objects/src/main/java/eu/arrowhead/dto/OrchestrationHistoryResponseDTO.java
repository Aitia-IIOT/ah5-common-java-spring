package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationHistoryResponseDTO(
		List<OrchestrationJobDTO> entries,
		long count) {

}
