package eu.arrowhead.dto;

import java.util.List;

public record SystemListResponseTerseDTO(
		List<SystemResopnseTerseDTO> entries,
		long count) {

}
