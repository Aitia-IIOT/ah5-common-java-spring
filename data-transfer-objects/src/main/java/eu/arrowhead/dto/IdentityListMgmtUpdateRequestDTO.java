package eu.arrowhead.dto;

import java.util.List;

public record IdentityListMgmtUpdateRequestDTO(
		List<IdentityMgmtRequestDTO> identities) {
}