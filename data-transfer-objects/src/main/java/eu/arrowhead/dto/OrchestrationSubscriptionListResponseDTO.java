package eu.arrowhead.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record OrchestrationSubscriptionListResponseDTO(
		List<OrchestrationSubscriptionResponseDTO> entries,
		long count) {

}
