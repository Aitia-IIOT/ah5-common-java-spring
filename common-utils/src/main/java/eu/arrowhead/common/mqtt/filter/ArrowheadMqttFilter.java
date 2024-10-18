package eu.arrowhead.common.mqtt.filter;

import eu.arrowhead.common.mqtt.model.MqttRequestModel;

public abstract class ArrowheadMqttFilter {

	public abstract int order();
	public abstract void doFilter(final String authKey, final MqttRequestModel request);

}
