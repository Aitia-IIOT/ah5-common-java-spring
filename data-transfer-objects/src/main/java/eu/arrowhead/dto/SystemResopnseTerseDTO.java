package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

public record SystemResopnseTerseDTO(
		String name,
		Map<String, Object> metadata,
		String version,
		List<AddressDTO> addresses,
		String deviceName,
		String createdAt,
		String updatedAt) {

}
