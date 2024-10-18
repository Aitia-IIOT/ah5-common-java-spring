package eu.arrowhead.dto;

public record MqttResponseTemplate(
		Integer status,
		String traceId,
		String receiver,
		Object payload) {
}
