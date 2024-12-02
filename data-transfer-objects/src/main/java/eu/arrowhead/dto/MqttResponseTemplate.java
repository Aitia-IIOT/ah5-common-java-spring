package eu.arrowhead.dto;

public record MqttResponseTemplate(
		int status,
		String traceId,
		String receiver,
		Object payload) {
}