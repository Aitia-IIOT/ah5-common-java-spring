package eu.arrowhead.dto;

import java.util.List;

public record DeviceQueryRequestDTO(
		PageDTO pagination,
		List<String> deviceNames,
		List<String> addresses,
		String addressType,
		List<MetadataRequirementDTO> metadataRequirementList) {
}