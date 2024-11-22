package eu.arrowhead.common.mqtt.filter.authorization;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

@Service
@ConditionalOnProperty(name = {"mqtt.api.enabled", "enable.management.filter"}, havingValue = "true", matchIfMissing = false)
public class ManagementServiceMqttFilter implements ArrowheadMqttFilter {

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.REQUEST_FILTER_ORDER_AUTHORIZATION_MGMT_SERVICE;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authKey, final MqttRequestModel request) {
		// TODO Auto-generated method stub

	}

}
