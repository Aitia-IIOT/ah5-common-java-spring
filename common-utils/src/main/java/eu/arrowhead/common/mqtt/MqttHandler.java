package eu.arrowhead.common.mqtt;

import eu.arrowhead.dto.MqttRequestTemplate;

public interface MqttHandler {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	void handle(final MqttRequestTemplate msgTemplate);
}