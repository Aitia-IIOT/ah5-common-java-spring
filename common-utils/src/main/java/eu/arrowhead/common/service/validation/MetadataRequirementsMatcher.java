package eu.arrowhead.common.service.validation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.arrowhead.dto.MetadataRequirementDTO;

public final class MetadataRequirementsMatcher {
	

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// Returns true if every requirement matches one of the given metadata. Otherwise returns false. 
	public static boolean isMetadataMatch(final Map<String, Object> metadata, final MetadataRequirementDTO requirements) {
		
		for (Entry<String, Object> requirement : requirements.entrySet()) {
			
			boolean match = false;
			
			for (Entry<String, Object> metadataEntry : metadata.entrySet()) {
				
				//check if keys match
				if (metadataEntry.getKey().equals(requirement.getKey())) {
					
					// check if values match
					// 1. case: requirement is a list
					if (requirement.getValue() instanceof List) {
						// this case the matching metadata has to be a list too
						if (!(metadataEntry.getValue() instanceof List)) {
							continue;
						}
						
						// we have to find a match for all required elements
						boolean allElementsMatch = true;
							
						for (Object requirementElement : (List)requirement.getValue()) {
							boolean elementMatch = false;
							for (Object metadataElement : (List)metadataEntry.getValue()) {
								if (metadataElement.equals(requirementElement)) {
									elementMatch = true;
								}
							}
							// if there was no match for one element, not all elements match
							if (!elementMatch) {
								allElementsMatch = false;
							}
						}
						
						if (allElementsMatch) {
							match = true;
						}
					} 
					// 2. case: requirement is not a list
					else {
						// if metadata is a list, it must contain the required element
						if (metadataEntry.getValue() instanceof List && ((List) metadataEntry.getValue()).contains(requirement.getValue())) {
							match = true;
						}
						
						// if both metadata and requirement are simple objects, they have to be equal
						else {
							if (metadataEntry.getValue().equals(requirement.getValue())) {
								match = true;
							}
						}
					}	
				}
			}
			
			// if there was no match for the requirement, we return false
			if (!match) {
				return false;
			}
			
		}
		
		return true;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private MetadataRequirementsMatcher() {
		throw new UnsupportedOperationException();
	}
}
