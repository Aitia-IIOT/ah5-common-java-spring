package eu.arrowhead.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record DeviceResponseDTO(
		String name,
		Map<String, Object> metadata,
		List<AddressDTO> addresses,
		String createdAt,
		String updatedAt) {

}
