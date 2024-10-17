package eu.arrowhead.common.mqtt.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

@Service
public class UnsupportedMqttTopicHandler extends MqttTopicHandler {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	//methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public String topic() {
		return Constants.MQTT_TOPIC_UNSUPPORTED;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void handle(final MqttRequestModel request) {
		logger.debug("UnsupportedMqttTopicHandler.handle started");

		ahMqttService.response(Constants.MQTT_SERVICE_PROVIDING_BROKER_CONNECT_ID, request.getRequester(), request.getResponseTopic(),
				request.getTraceId(), request.getQosRequirement(), false, "Unsupported topic: " + request.getRequestTopic());
	}
}
