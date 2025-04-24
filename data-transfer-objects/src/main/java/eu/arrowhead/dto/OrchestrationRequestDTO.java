package eu.arrowhead.dto;

import java.util.HashMap;
import java.util.Map;

public record OrchestrationRequestDTO(
		OrchestrationServiceRequirementDTO serviceRequirement,
		Map<String, Boolean> orchestrationFlags,
		Map<String, String> qosRequirements,
		Integer exclusivityDuration) {

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private OrchestrationServiceRequirementDTO serviceRequirement;
		private Map<String, Boolean> orchestrationFlags;
		private Map<String, String> qosRequirements;
		private Integer exclusivityDuration;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder serviceRequirement(final OrchestrationServiceRequirementDTO serviceRequirement) {
			this.serviceRequirement = serviceRequirement;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder orchestrationFlags(final Map<String, Boolean> orchestrationFlags) {
			this.orchestrationFlags = orchestrationFlags;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder orchestrationFlag(final String orchestrationFlag, final boolean value) {
			if (this.orchestrationFlags == null) {
				this.orchestrationFlags = new HashMap<>();
			}
			this.orchestrationFlags.put(orchestrationFlag, value);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder qosRequirements(final Map<String, String> qosRequirements) {
			this.qosRequirements = qosRequirements;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder qosRequirement(final String key, final String value) {
			if (this.qosRequirements == null) {
				this.qosRequirements = new HashMap<>();
			}
			this.qosRequirements.put(key, value);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder exclusivityDuration(final Integer exclusivityDuration) {
			this.exclusivityDuration = exclusivityDuration;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public OrchestrationRequestDTO build() {
			return new OrchestrationRequestDTO(serviceRequirement, orchestrationFlags, qosRequirements, exclusivityDuration);
		}
	}
}
