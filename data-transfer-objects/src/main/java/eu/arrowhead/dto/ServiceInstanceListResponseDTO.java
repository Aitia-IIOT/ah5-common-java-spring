package eu.arrowhead.dto;

import java.util.List;

public record ServiceInstanceListResponseDTO(
		List<ServiceInstanceResponseDTO> entries,
		long count) {

}
