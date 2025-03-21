package eu.arrowhead.dto;

public record MqttPublishTemplate(
		String sender,
		String opration,
		Object payload) {

}
