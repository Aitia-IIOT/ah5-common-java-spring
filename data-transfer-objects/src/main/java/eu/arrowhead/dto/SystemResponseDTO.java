package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record SystemResponseDTO(
		String name,
		Map<String, Object> metadata,
		String version,
		List<AddressDTO> addresses,
		DeviceResponseDTO device,
		String createdAt,
		String updatedAt) {

}
