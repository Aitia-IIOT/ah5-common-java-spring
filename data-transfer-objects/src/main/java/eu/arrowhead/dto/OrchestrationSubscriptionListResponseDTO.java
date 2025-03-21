package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationSubscriptionListResponseDTO(
		List<OrchestrationSubscriptionResponseDTO> entries,
		long count) {

}
