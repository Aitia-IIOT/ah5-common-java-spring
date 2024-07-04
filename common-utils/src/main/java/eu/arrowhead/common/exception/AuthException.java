package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class AuthException extends ArrowheadException {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public AuthException(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.AUTH);
	}

	//-------------------------------------------------------------------------------------------------
	public AuthException(final String msg, final String origin) {
		super(msg, origin);
		this.setExceptionType(ExceptionType.AUTH);
	}

	//-------------------------------------------------------------------------------------------------
	public AuthException(final String msg, final Throwable cause) {
		super(msg, cause);
		this.setExceptionType(ExceptionType.AUTH);
	}

	//-------------------------------------------------------------------------------------------------
	public AuthException(final String msg) {
		super(msg);
		this.setExceptionType(ExceptionType.AUTH);
	}
}