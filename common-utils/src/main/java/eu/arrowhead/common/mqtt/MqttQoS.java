package eu.arrowhead.common.mqtt;

public enum MqttQoS {

	AT_MOST_ONCE(0), AT_LEAST_ONCE(1), EXACTLY_ONCE(2);

	//=================================================================================================
	// members

	private final int value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private MqttQoS(final int value) {
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public int value() {
		return value;
	}

	//-------------------------------------------------------------------------------------------------
	public static MqttQoS valueOf(final int value) {
		switch (value) {
		case 1:
			return AT_LEAST_ONCE;

		case 2:
			return EXACTLY_ONCE;

		default:
			return AT_MOST_ONCE;
		}
	}
}
