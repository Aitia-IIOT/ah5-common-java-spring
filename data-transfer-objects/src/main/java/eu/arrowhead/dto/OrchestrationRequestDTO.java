package eu.arrowhead.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record OrchestrationRequestDTO(
		OrchestrationServiceRequirementDTO serviceRequirement,
		List<String> orchestrationFlags,
		Map<String, String> qosRequirements,
		Integer exclusivityDuration) {

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public class Builder {

		//=================================================================================================
		// members

		private OrchestrationServiceRequirementDTO serviceRequirement;
		private List<String> orchestrationFlags;
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
		public Builder orchestrationFlags(final List<String> orchestrationFlags) {
			this.orchestrationFlags = orchestrationFlags;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder orchestrationFlag(final String orchestrationFlag) {
			if (this.orchestrationFlags == null) {
				this.orchestrationFlags = new ArrayList<>();
			}
			this.orchestrationFlags.add(orchestrationFlag);
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
