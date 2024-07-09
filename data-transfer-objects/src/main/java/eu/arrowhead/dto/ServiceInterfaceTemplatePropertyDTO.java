package eu.arrowhead.dto;

import java.util.List;

public record ServiceInterfaceTemplatePropertyDTO(
		String name,
		boolean mandatory,
		String validator,
		List<String> validatorParams) {

}
