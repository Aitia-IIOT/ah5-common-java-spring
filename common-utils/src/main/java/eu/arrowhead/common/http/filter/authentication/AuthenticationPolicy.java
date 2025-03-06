package eu.arrowhead.common.http.filter.authentication;

public enum AuthenticationPolicy {
	DECLARED, CERTIFICATE, OUTSOURCED;

	//=================================================================================================
	// members

	public static final String CERTIFICATE_VALUE = "CERTIFICATE"; // right side must be a constant expression
}