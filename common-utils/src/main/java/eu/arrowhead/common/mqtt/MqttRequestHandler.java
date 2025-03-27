package eu.arrowhead.common.mqtt;

import eu.arrowhead.dto.MqttRequestTemplate;

public interface MqttRequestHandler {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	void handle(final MqttRequestTemplate msgTemplate);
}