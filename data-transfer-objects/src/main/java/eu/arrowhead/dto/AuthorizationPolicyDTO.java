package eu.arrowhead.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationPolicyType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationPolicyDTO(
		AuthorizationPolicyType policyType,
		List<String> policyList,
		MetadataRequirementDTO policyMetadataRequirement) {
}