package eu.arrowhead.common.mqtt;

import eu.arrowhead.dto.MqttRequestTemplate;

public interface MqttHandler {

	void handle(final MqttRequestTemplate msgTemplate);
}
