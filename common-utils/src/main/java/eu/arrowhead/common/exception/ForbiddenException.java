package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class ForbiddenException extends ArrowheadException {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public ForbiddenException(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.FORBIDDEN);
	}
	
	//-------------------------------------------------------------------------------------------------
	public ForbiddenException(final String msg, final String origin) {
	    super(msg, origin);
	    this.setExceptionType(ExceptionType.FORBIDDEN);
	}
	
	//-------------------------------------------------------------------------------------------------
	public ForbiddenException(final String msg, final Throwable cause) {
	    super(msg, cause);
	    this.setExceptionType(ExceptionType.FORBIDDEN);
	}
	
	//-------------------------------------------------------------------------------------------------
	public ForbiddenException(final String msg) {
	    super(msg);
	    this.setExceptionType(ExceptionType.FORBIDDEN);
	}
}