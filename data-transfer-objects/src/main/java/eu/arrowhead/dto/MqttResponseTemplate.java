package eu.arrowhead.dto;

public record MqttResponseTemplate(
		boolean success,
		String traceId,
		String receiver,
		Object payload) {
}
