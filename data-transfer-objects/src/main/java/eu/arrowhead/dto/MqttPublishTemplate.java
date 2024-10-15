package eu.arrowhead.dto;

public record MqttPublishTemplate(
		boolean success,
		String traceId,
		String receiver,
		Object payload) {
}
