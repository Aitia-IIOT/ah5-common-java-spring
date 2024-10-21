package eu.arrowhead.common.mqtt.filter.authentication;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

public class SelfDeclaredMqttFilter extends ArrowheadMqttFilter {

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.MQTT_AUTHENTICATION_FILTER_ORDER;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authInfo, final MqttRequestModel request) {
		if (Utilities.isEmpty(authInfo)) {
			throw new AuthException("No authorization info has been provided");
		}

		final String[] split = authInfo.split(Constants.MQTT_AUTH_IFNO_DELIMITER);
		if (split.length != 2 || !split[0].equals(Constants.MQTT_AUTH_IFNO_PREFIX_SYSTEM)) {
			throw new AuthException("Invalid authorization info");
		}

		request.setRequester(split[1]);
		
		// TODO SYSOP beállítani
	}
}
