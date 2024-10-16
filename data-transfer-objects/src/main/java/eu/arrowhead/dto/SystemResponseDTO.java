package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record SystemResponseDTO(
		String name,
		Map<String, Object> metadata,
		String version,
		List<AddressDTO> addresses,
		DeviceResponseDTO device,
		String createdAt,
		String updatedAt) {

}
