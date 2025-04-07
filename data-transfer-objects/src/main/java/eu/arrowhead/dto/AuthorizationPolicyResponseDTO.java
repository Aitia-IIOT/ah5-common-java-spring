package eu.arrowhead.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationLevel;
import eu.arrowhead.dto.enums.AuthorizationTargetType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationPolicyResponseDTO(
		String instanceId,
		AuthorizationLevel level,
		String cloud,
		String provider,
		AuthorizationTargetType targetType,
		String target,
		String description,
		AuthorizationPolicyDTO defaultPolicy,
		Map<String, AuthorizationPolicyDTO> scopedPolicies,
		String createdBy,
		String createdAt) {
}
