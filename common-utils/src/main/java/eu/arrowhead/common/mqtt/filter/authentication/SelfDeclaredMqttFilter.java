package eu.arrowhead.common.mqtt.filter.authentication;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

public class SelfDeclaredMqttFilter implements ArrowheadMqttFilter {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.REQUEST_FILTER_ORDER_AUTHENTICATION;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authInfo, final MqttRequestModel request) {
		if (Utilities.isEmpty(authInfo)) {
			throw new AuthException("No authentication info has been provided");
		}

		final String[] split = authInfo.split(Constants.MQTT_AUTH_INFO_DELIMITER);
		if (split.length != 2 || !split[0].equals(Constants.MQTT_AUTH_INFO_PREFIX_SYSTEM)) {
			throw new AuthException("Invalid authentication info");
		}

		request.setRequester(split[1]);
		request.setSysOp(Constants.SYSOP.equals(split[1].toLowerCase().trim()));
	}
}