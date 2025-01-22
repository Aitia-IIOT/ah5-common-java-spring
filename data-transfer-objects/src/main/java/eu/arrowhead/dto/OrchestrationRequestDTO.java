package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record OrchestrationRequestDTO(
		OrchestrationServiceRequirementDTO serviceRequirement,
		List<String> orchestrationFlags,
		Map<String, String> qosRequirements,
		Integer exclusivityDuration) {

}
