package eu.arrowhead.dto;

import java.util.List;

public record ServiceInstanceUpdateListRequestDTO(
		List<ServiceInstanceUpdateRequestDTO> instances) {

}
