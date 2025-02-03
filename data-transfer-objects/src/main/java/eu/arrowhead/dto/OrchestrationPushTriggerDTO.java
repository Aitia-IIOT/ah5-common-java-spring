package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationPushTriggerDTO(
		List<String> tartgetSystems,
		List<String> subscriptionIds) {

}
