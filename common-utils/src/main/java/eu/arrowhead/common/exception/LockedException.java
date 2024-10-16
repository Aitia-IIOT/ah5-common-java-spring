package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class LockedException extends ArrowheadException {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LockedException(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.LOCKED);
	}

	//-------------------------------------------------------------------------------------------------
	public LockedException(final String msg, final String origin) {
		super(msg, origin);
		this.setExceptionType(ExceptionType.LOCKED);
	}

	//-------------------------------------------------------------------------------------------------
	public LockedException(final String msg, final Throwable cause) {
		super(msg, cause);
		this.setExceptionType(ExceptionType.LOCKED);
	}

	//-------------------------------------------------------------------------------------------------
	public LockedException(final String msg) {
		super(msg);
		this.setExceptionType(ExceptionType.LOCKED);
	}
}