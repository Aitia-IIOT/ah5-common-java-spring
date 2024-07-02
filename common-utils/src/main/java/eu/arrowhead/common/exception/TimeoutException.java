package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class TimeoutException extends ArrowheadException {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public TimeoutException(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.TIMEOUT);
	}
	
	//-------------------------------------------------------------------------------------------------
	public TimeoutException(final String msg, final String origin) {
	    super(msg, origin);
	    this.setExceptionType(ExceptionType.TIMEOUT);
	}
	
	//-------------------------------------------------------------------------------------------------
	public TimeoutException(final String msg, final Throwable cause) {
	    super(msg, cause);
	    this.setExceptionType(ExceptionType.TIMEOUT);
	}
	
	//-------------------------------------------------------------------------------------------------
	public TimeoutException(final String msg) {
	    super(msg);
	    this.setExceptionType(ExceptionType.TIMEOUT);
	}
}