package eu.arrowhead.dto.enums;

public enum ExceptionType {
	ARROWHEAD(500),
	INVALID_PARAMETER(400),
	AUTH(401),
	FORBIDDEN(403),
	DATA_NOT_FOUND(404),
	TIMEOUT(408),
	INTERNAL_SERVER_ERROR(500),
	EXTERNAL_SERVER_ERROR(503);
	
	//=================================================================================================
	// members
	
	final int errorCode;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ExceptionType(final int errorCode) {
		this.errorCode = errorCode;
	}

	//-------------------------------------------------------------------------------------------------
	public int getErrorCode() { return errorCode; }
}

