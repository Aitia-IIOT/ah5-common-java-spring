package eu.arrowhead.dto;

import java.util.List;

public record DeviceListRequestDTO(
		List<DeviceRequestDTO> devices) {

}
