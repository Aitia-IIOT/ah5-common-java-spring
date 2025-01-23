package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record IdentityMgmtResponseDTO(
		String systemName,
		String authenticationMethod,
		boolean sysop,
		String createdBy,
		String createdAt,
		String updatedBy,
		String updatedAt
		// credentials and extra field is omitted intentionally
		) {

}
