package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class ExternalServerError extends ArrowheadException {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public ExternalServerError(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.EXTERNAL_SERVER_ERROR);
	}
	
	//-------------------------------------------------------------------------------------------------
	public ExternalServerError(final String msg, final String origin) {
	    super(msg, origin);
	    this.setExceptionType(ExceptionType.EXTERNAL_SERVER_ERROR);
	}
	
	//-------------------------------------------------------------------------------------------------
	public ExternalServerError(final String msg, final Throwable cause) {
	    super(msg, cause);
	    this.setExceptionType(ExceptionType.EXTERNAL_SERVER_ERROR);
	}
	
	//-------------------------------------------------------------------------------------------------
	public ExternalServerError(final String msg) {
	    super(msg);
	    this.setExceptionType(ExceptionType.EXTERNAL_SERVER_ERROR);
	}
}