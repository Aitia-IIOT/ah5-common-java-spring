package eu.arrowhead.dto;

import java.util.List;

public record IdentityListMgmtRequestDTO(
		List<IdentityMgmtRequestDTO> identites) {
}