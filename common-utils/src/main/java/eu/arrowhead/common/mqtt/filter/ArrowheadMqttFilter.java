package eu.arrowhead.common.mqtt.filter;

import eu.arrowhead.common.mqtt.model.MqttRequestModel;

public interface ArrowheadMqttFilter {

	//-------------------------------------------------------------------------------------------------
	public int order();

	//-------------------------------------------------------------------------------------------------
	public void doFilter(final String authKey, final MqttRequestModel request);
}