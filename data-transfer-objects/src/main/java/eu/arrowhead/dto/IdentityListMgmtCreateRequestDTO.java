package eu.arrowhead.dto;

import java.util.List;

public record IdentityListMgmtCreateRequestDTO(
		String authenticationMethod,
		List<IdentityMgmtRequestDTO> identities) {
}