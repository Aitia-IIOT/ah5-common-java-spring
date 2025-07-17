package eu.arrowhead.common.mqtt.handler;

import eu.arrowhead.common.mqtt.MqttResourceManager;
import eu.arrowhead.common.mqtt.model.MqttMessageContainer;

public record MqttMessageContainerHandlerContext(
		MqttMessageContainer msgContainer,
		MqttTopicHandler topicHandler,
		MqttResourceManager resourceManager) {
}