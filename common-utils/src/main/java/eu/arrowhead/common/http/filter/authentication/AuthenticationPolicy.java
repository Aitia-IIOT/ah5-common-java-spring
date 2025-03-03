package eu.arrowhead.common.http.filter.authentication;

public enum AuthenticationPolicy {
	DECLARED, CERTIFICATE, OUTSOURCED, INTERNAL;

	//=================================================================================================
	// members

	public static final String CERTIFICATE_VALUE = "CERTIFICATE"; // right side must be a constant expression
	public static final String INTERNAL_VALUE = "INTERNAL"; // right side must be a constant expression
	public static final String OUTSOURCED_VALUE = "OUTSOURCED"; // right side must be a constant expression

}