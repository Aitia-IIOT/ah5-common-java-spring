package eu.arrowhead.dto;

import java.util.List;

public record BlacklistCreateListRequestDTO(
		List<BlacklistCreateRequestDTO> entities
		) {

}
