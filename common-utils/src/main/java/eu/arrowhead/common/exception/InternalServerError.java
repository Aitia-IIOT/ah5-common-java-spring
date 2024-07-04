package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class InternalServerError extends ArrowheadException {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public InternalServerError(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.INTERNAL_SERVER_ERROR);
	}

	//-------------------------------------------------------------------------------------------------
	public InternalServerError(final String msg, final String origin) {
		super(msg, origin);
		this.setExceptionType(ExceptionType.INTERNAL_SERVER_ERROR);
	}

	//-------------------------------------------------------------------------------------------------
	public InternalServerError(final String msg, final Throwable cause) {
		super(msg, cause);
		this.setExceptionType(ExceptionType.INTERNAL_SERVER_ERROR);
	}

	//-------------------------------------------------------------------------------------------------
	public InternalServerError(final String msg) {
		super(msg);
		this.setExceptionType(ExceptionType.INTERNAL_SERVER_ERROR);
	}
}