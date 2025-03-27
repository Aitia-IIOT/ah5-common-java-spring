package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationLockListRequestDTO(
		List<OrchestrationLockRequestDTO> locks) {

}
