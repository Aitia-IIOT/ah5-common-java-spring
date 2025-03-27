package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationResponseDTO(
		List<OrchestrationResultDTO> results,
		List<String> warnings) {
}
