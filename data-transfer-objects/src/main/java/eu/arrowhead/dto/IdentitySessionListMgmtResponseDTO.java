package eu.arrowhead.dto;

import java.util.List;

public record IdentitySessionListMgmtResponseDTO(
		List<IdentitySessionResponseDTO> sessions,
		long count) {
}