package eu.arrowhead.dto.enums;

public enum AuthorizationTokenType {
	TIME_LIMITED_TOKEN, USAGE_LIMITED_TOKEN, JSON_WEB_TOKEN;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static AuthorizationTokenType fromServiceInterfacePolicy(final ServiceInterfacePolicy policy) {
		if (policy == null) {
			return null;
		}

		switch (policy) {
		case NONE:
		case CERT_AUTH:
			return null;
		case JSON_WEB_TOKEN_AUTH:
			return JSON_WEB_TOKEN;
		case TIME_LIMITED_TOKEN_AUTH:
			return TIME_LIMITED_TOKEN;
		case USAGE_LIMITED_TOKEN_AUTH:
			return USAGE_LIMITED_TOKEN;
		default:
			throw new IllegalArgumentException("Unknown service interface policy: " + policy.name());
		}
	}
}
