package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationMgmtGrantListRequestDTO(
		List<AuthorizationMgmtGrantRequestDTO> list) {
}