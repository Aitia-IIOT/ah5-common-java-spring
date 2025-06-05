package eu.arrowhead.dto.enums;

public enum AuthorizationLevel {
	MGMT("MGMT"), PROVIDER("PR");

	//=================================================================================================
	// members

	private String prefix;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String getPrefix() {
		return prefix;
	}

	//-------------------------------------------------------------------------------------------------
	public static AuthorizationLevel fromPrefix(final String prefix) {
		return switch (prefix) {
		case "MGMT" -> AuthorizationLevel.MGMT;
		case "PR" -> AuthorizationLevel.PROVIDER;
		case null, default -> null;
		};
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private AuthorizationLevel(final String prefix) {
		this.prefix = prefix;
	}
}