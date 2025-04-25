package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationSimpleStoreListResponseDTO(
		List<OrchestrationSimpleStoreResponseDTO> entries,
		long count) {

}
