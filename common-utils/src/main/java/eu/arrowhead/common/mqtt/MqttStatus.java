package eu.arrowhead.common.mqtt;

public enum MqttStatus {

	OK(200),
	CREATED(201),
	NO_CONTENT(204),
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	FORBIDDEN(403),
	NOT_FOUND(404),
	TIMEOUT(408),
	INTERNAL_SERVER_ERROR(500),
	EXTERNAL_SERVER_ERROR(503);

	//=================================================================================================
	// members

	private final int value;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private MqttStatus(final int value) {
		this.value = value;
	}

	//-------------------------------------------------------------------------------------------------
	public int value() {
		return value;
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MagicNumber")
	public static MqttStatus resolve(final int value) {
		switch (value) {
		case 200:
			return OK;
		case 201:
			return CREATED;
		case 204:
			return NO_CONTENT;
		case 400:
			return BAD_REQUEST;
		case 401:
			return UNAUTHORIZED;
		case 403:
			return FORBIDDEN;
		case 404:
			return NOT_FOUND;
		case 408:
			return TIMEOUT;
		case 500:
			return INTERNAL_SERVER_ERROR;
		case 503:
			return EXTERNAL_SERVER_ERROR;
		default:
			return null;
		}
	}
}
