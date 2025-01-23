package eu.arrowhead.dto;

import java.util.Map;

public record IdentityMgmtRequestDTO(
		String systemName,
		String authenticationMethod,
		Map<String, String> credentials,
		Boolean sysop) implements ISystemName {
}