package eu.arrowhead.dto;

public record MqttRequestTemplate(
		String traceId,
		String operation,
		String authentication,
		String responseTopic,
		Integer qosRequirement,
		Object payload) {
}
