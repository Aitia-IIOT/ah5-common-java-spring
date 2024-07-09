package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record DeviceRequestDTO(
		String name,
		Map<String, Object> metadata,
		List<AddressDTO> addresses) {

}
