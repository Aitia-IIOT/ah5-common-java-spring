package eu.arrowhead.dto;

public record MqttPublishTemplate(
		String sender,
		Object payload) {

}
