package eu.arrowhead.dto;

import java.util.List;

public record DeviceLookupRequestDTO(
		List<String> deviceNames,
		List<String> addresses,
		String addressType,
		List<MetadataRequirementDTO> metadataRequirementList) {

}
