package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationPushTriggerDTO(
		List<String> targetSystems,
		List<String> subscriptionIds) {

}
