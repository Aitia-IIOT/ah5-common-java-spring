package eu.arrowhead.dto;

import java.util.Map;

public record IdentityMgmtRequestDTO(
		String systemName,
		Map<String, String> credentials,
		Boolean sysop) implements ISystemName {
}