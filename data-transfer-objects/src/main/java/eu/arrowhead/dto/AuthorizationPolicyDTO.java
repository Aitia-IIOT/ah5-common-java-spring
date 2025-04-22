package eu.arrowhead.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.AuthorizationPolicyType;

@JsonInclude(Include.NON_NULL)
public record AuthorizationPolicyDTO(
		String scope,
		String policyType,
		List<String> policyList,
		MetadataRequirementDTO policyMetadataRequirement) {

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public class Builder {

		//=================================================================================================
		// members

		private String scope = DTODefaults.DEFAULT_AUTHORIZATION_SCOPE;
		private final AuthorizationPolicyType policyType;
		private List<String> policyList;
		private MetadataRequirementDTO policyMetadataRequirement;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder(final AuthorizationPolicyType policyType) {
			this.policyType = policyType;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder scope(final String scope) {
			this.scope = scope == null || scope.isBlank() ? DTODefaults.DEFAULT_AUTHORIZATION_SCOPE : scope;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder policyList(final List<String> list) {
			switch (policyType) {
			case ALL:
			case SYS_METADATA:
				throw new IllegalArgumentException("Policy type " + policyType.name() + " does not support lists");
			case BLACKLIST:
			case WHITELIST:
				this.policyList = list;
				break;
			default:
				// intentionally do nothing
			}

			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder addPolicyListElement(final String systemName) {
			switch (policyType) {
			case ALL:
			case SYS_METADATA:
				throw new IllegalArgumentException("Policy type " + policyType.name() + " does not support lists");
			case BLACKLIST:
			case WHITELIST:
				if (this.policyList == null) {
					this.policyList = new ArrayList<>();
				}
				this.policyList.add(systemName);
				break;
			default:
				// intentionally do nothing
			}

			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder blacklist(final List<String> blacklist) {
			if (AuthorizationPolicyType.BLACKLIST != this.policyType) {
				throw new IllegalArgumentException("Policy type is not " + AuthorizationPolicyType.BLACKLIST.name());
			}

			return policyList(blacklist);
		}

		//-------------------------------------------------------------------------------------------------
		public Builder addBlacklistElement(final String systemName) {
			if (AuthorizationPolicyType.BLACKLIST != this.policyType) {
				throw new IllegalArgumentException("Policy type is not " + AuthorizationPolicyType.BLACKLIST.name());
			}

			return addPolicyListElement(systemName);
		}

		//-------------------------------------------------------------------------------------------------
		public Builder whitelist(final List<String> whitelist) {
			if (AuthorizationPolicyType.WHITELIST != this.policyType) {
				throw new IllegalArgumentException("Policy type is not " + AuthorizationPolicyType.WHITELIST.name());
			}

			return policyList(whitelist);
		}

		//-------------------------------------------------------------------------------------------------
		public Builder addWhitelistElement(final String systemName) {
			if (AuthorizationPolicyType.WHITELIST != this.policyType) {
				throw new IllegalArgumentException("Policy type is not " + AuthorizationPolicyType.WHITELIST.name());
			}

			return addPolicyListElement(systemName);
		}

		//-------------------------------------------------------------------------------------------------
		public Builder policyMetadataRequirement(final MetadataRequirementDTO metadataRequirement) {
			switch (policyType) {
			case ALL:
			case BLACKLIST:
			case WHITELIST:
				throw new IllegalArgumentException("Policy type " + policyType.name() + " does not support metadata requirement");
			case SYS_METADATA:
				this.policyMetadataRequirement = metadataRequirement;
				break;
			default:
				// intentionally do nothing
			}

			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public AuthorizationPolicyDTO build() {
			switch (policyType) {
			case ALL:
				return new AuthorizationPolicyDTO(this.scope, policyType.name(), null, null);
			case BLACKLIST:
			case WHITELIST:
				if (policyList == null || policyList.isEmpty()) {
					throw new IllegalArgumentException("Policy type " + this.policyType.name() + " requires a list at least with one system name");
				}
				return new AuthorizationPolicyDTO(this.scope, this.policyType.name(), this.policyList, null);
			case SYS_METADATA:
				if (policyMetadataRequirement == null || policyMetadataRequirement.isEmpty()) {
					throw new IllegalArgumentException("Policy type " + this.policyType.name() + " requires metadata requirement");
				}
				return new AuthorizationPolicyDTO(this.scope, this.policyType.name(), null, this.policyMetadataRequirement);
			default:
				throw new IllegalArgumentException("Unknown policy type " + this.policyType.name());
			}
		}
	}
}