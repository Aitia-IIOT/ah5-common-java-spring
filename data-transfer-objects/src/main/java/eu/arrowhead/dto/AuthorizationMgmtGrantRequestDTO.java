package eu.arrowhead.dto;

import java.util.HashMap;
import java.util.Map;

import eu.arrowhead.dto.enums.AuthorizationTargetType;

public record AuthorizationMgmtGrantRequestDTO(
		String cloud,
		String provider,
		String targetType,
		String target,
		String description,
		AuthorizationPolicyRequestDTO defaultPolicy,
		Map<String, AuthorizationPolicyRequestDTO> scopedPolicies) {

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public class Builder {

		//=================================================================================================
		// members

		private String cloud = DTODefaults.DEFAULT_CLOUD;
		private String provider;
		private final AuthorizationTargetType targetType;
		private String target;
		private String description;
		private AuthorizationPolicyRequestDTO defaultPolicy;
		private Map<String, AuthorizationPolicyRequestDTO> scopedPolicies;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder(final AuthorizationTargetType targetType) {
			this.targetType = targetType;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder cloud(final String cloud) {
			this.cloud = cloud == null || cloud.isBlank() ? DTODefaults.DEFAULT_CLOUD : cloud;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder provider(final String provider) {
			this.provider = provider;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder target(final String target) {
			this.target = target;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder description(final String description) {
			this.description = description;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder defaultPolicy(final AuthorizationPolicyRequestDTO defaultPolicy) {
			this.defaultPolicy = defaultPolicy;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder scopedPolicy(final String scope, final AuthorizationPolicyRequestDTO policy) {
			if (this.scopedPolicies == null) {
				this.scopedPolicies = new HashMap<>();
			}

			if (scope != null) {
				final boolean isDefault = DTODefaults.DEFAULT_AUTHORIZATION_SCOPE.equals(scope.trim());
				if (isDefault) {
					this.defaultPolicy = policy;
				} else {
					this.scopedPolicies.put(scope.trim(), policy);
				}
			}

			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder scopedPolicies(final Map<String, AuthorizationPolicyRequestDTO> scopedPolicies) {
			this.scopedPolicies = scopedPolicies;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public AuthorizationMgmtGrantRequestDTO build() {
			return new AuthorizationMgmtGrantRequestDTO(
					this.cloud,
					this.provider,
					this.targetType.name(),
					this.target,
					this.description,
					this.defaultPolicy,
					this.scopedPolicies);
		}
	}
}