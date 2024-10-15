package eu.arrowhead.dto;

import java.util.Map;

public record MqttRequestTemplate(
		String traceId,
		String operation,
		String authentication,
		String responseTopic,
		Integer qosRequirement,
		Map<String, Object> payload) {
}
