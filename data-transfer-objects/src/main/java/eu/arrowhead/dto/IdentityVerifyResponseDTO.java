package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record IdentityVerifyResponseDTO(
		boolean verified,
		String systemName,
		Boolean sysop,
		String loginTime,
		String expirationTime) {
}