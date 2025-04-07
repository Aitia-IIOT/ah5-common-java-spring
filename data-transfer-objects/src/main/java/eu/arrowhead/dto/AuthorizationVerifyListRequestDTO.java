package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationVerifyListRequestDTO(
		List<AuthorizationVerifyRequestDTO> list) {
}
