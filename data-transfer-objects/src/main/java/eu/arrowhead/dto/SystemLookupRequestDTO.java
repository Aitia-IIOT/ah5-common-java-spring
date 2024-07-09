package eu.arrowhead.dto;

import java.util.List;

public record SystemLookupRequestDTO(
		List<String> systemNames,
		List<String> addresses,
		String addressType,
		List<MetadataRequirementDTO> metadataRequirementList,
		List<String> versions,
		List<String> deviceNames) {

}
