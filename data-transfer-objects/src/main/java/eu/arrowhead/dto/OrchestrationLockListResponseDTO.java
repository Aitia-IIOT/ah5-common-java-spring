package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationLockListResponseDTO(
		List<OrchestrationLockResponseDTO> entries,
		long count) {

}
