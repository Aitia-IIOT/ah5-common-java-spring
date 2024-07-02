package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class DataNotFoundException extends ArrowheadException {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public DataNotFoundException(final String msg, final String origin, final Throwable cause) {
		super(msg, origin, cause);
		this.setExceptionType(ExceptionType.DATA_NOT_FOUND);
	}
	
	//-------------------------------------------------------------------------------------------------
	public DataNotFoundException(final String msg, final String origin) {
	    super(msg, origin);
	    this.setExceptionType(ExceptionType.DATA_NOT_FOUND);
	}
	
	//-------------------------------------------------------------------------------------------------
	public DataNotFoundException(final String msg, final Throwable cause) {
	    super(msg, cause);
	    this.setExceptionType(ExceptionType.DATA_NOT_FOUND);
	}
	
	//-------------------------------------------------------------------------------------------------
	public DataNotFoundException(final String msg) {
	    super(msg);
	    this.setExceptionType(ExceptionType.DATA_NOT_FOUND);
	}
}