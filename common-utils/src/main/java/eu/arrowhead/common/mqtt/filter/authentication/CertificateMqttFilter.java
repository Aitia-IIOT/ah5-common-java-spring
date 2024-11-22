package eu.arrowhead.common.mqtt.filter.authentication;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

public class CertificateMqttFilter implements ArrowheadMqttFilter {

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.REQUEST_FILTER_ORDER_AUTHENTICATION;
	}

	//---------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authKey, final MqttRequestModel request) {
		// TODO Auto-generated method stub
	}

}
