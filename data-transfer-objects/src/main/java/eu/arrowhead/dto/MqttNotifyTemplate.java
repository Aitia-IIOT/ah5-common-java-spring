package eu.arrowhead.dto;

public record MqttNotifyTemplate(
		String receiver,
		String sender,
		Object payload) {

}
