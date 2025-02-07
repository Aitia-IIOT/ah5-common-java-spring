package eu.arrowhead.common.http.filter.authentication;

public enum AuthenticationPolicy {
	DECLARED, CERTIFICATE, OUTSOURCED, INTERNAL;

	//=================================================================================================
	// members

	public static final String OUTSOURCED_VALUE = "outsourced"; // right side must be a constant expression
	public static final String INTERNAL_VALUE = "internal"; // right side must be a constant expression
}