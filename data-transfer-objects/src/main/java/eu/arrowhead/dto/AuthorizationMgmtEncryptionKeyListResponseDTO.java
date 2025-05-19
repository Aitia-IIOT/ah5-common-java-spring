package eu.arrowhead.dto;

import java.util.List;

public record AuthorizationMgmtEncryptionKeyListResponseDTO(
			List<AuthorizationMgmtEncryptionKeyResponseDTO> entries,
			Long count
		) {

}
