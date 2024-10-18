package eu.arrowhead.common.mqtt.filter.authorization;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

@Service
@ConditionalOnExpression("${mqtt.api.enabled:true} && ${enable.blacklist.filter:true}")
public class BlacklistMqttFilter extends ArrowheadMqttFilter {

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.MQTT_AUTHORIZATION_BLACKLIST_FILTER_ORDER;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authKey, final MqttRequestModel request) {
		// TODO Auto-generated method stub
		System.out.println("MQTT blacklist filter");
	}

}
