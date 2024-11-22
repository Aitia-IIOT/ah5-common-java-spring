package eu.arrowhead.common.mqtt;

import eu.arrowhead.common.exception.InvalidParameterException;

public enum MqttQoS {

	AT_MOST_ONCE(0), AT_LEAST_ONCE(1), EXACTLY_ONCE(2);

	//=================================================================================================
	// members

	private final int value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public int value() {
		return value;
	}

	//-------------------------------------------------------------------------------------------------
	public static MqttQoS valueOf(final int value) {
		switch (value) {
		case 0:
			return AT_MOST_ONCE;
		case 1:
			return AT_LEAST_ONCE;

		case 2:
			return EXACTLY_ONCE;

		default:
			throw new InvalidParameterException("Unkown MQTT QoS value:" + value);
		}
	}

	//=================================================================================================
	//  assistant method

	//-------------------------------------------------------------------------------------------------
	private MqttQoS(final int value) {
		this.value = value;
	}
}
