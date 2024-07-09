package eu.arrowhead.dto;

import java.util.List;

public record DeviceListResponseDTO(
		List<DeviceResponseDTO> entries,
		long count) {

}
