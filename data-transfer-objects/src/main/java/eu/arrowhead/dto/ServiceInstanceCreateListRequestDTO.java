package eu.arrowhead.dto;

import java.util.List;

public record ServiceInstanceCreateListRequestDTO(
		List<ServiceInstanceCreateRequestDTO> instances) {

}
