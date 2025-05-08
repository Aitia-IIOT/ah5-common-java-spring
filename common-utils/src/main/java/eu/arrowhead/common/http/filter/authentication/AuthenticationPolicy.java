package eu.arrowhead.common.http.filter.authentication;

public enum AuthenticationPolicy {
	DECLARED, CERTIFICATE, OUTSOURCED, INTERNAL;

	//=================================================================================================
	// members

	public static final String CERTIFICATE_VALUE = "CERTIFICATE"; // right side must be a constant expression
	public static final String OUTSOURCED_VALUE = "OUTSOURCED"; // right side must be a constant expression

	// it is important to keep this lower-case (because @ConditionalOnExpression in CommonBeanConfig class is case sensitive)
	public static final String INTERNAL_VALUE = "internal"; // right side must be a constant expression

}