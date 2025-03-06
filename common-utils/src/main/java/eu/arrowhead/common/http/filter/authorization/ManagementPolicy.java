package eu.arrowhead.common.http.filter.authorization;

public enum ManagementPolicy {
	SYSOP_ONLY, WHITELIST, AUTHORIZATION;

	//=================================================================================================
	// members

	public static final String SYSOP_ONLY_VALUE = "SYSOP_ONLY"; // right side must be a constant expression
}