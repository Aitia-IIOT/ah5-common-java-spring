package eu.arrowhead.common.security;

public enum CertificateProfileType {
	MASTER("ma"),
	GATE("ga"),
	ORGANIZATION("or"),
	LOCAL_CLOUD("lo"),
	ON_BOARDING("on"),
	DEVICE("de"),
	SYSTEM("sy"),
	OPERATOR("op");

	//=================================================================================================
	// members

	private final String code;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String getCode() {
		return code;
	}

	//-------------------------------------------------------------------------------------------------
	public static CertificateProfileType fromCode(final String code) {
		for (final CertificateProfileType candidate : CertificateProfileType.values()) {
			if (candidate.getCode().equals(code)) {
				return candidate;
			}
		}

		return null;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private CertificateProfileType(final String code) {
		this.code = code;
	}
}