package eu.arrowhead.dto;

import java.util.List;

public record IdentityListMgmtRequestDTO(
		String authenticationMethod,
		List<IdentityMgmtRequestDTO> identities) {
}