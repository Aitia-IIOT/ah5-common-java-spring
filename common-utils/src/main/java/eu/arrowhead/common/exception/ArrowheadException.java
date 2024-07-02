package eu.arrowhead.common.exception;

import eu.arrowhead.dto.enums.ExceptionType;

@SuppressWarnings("serial")
public class ArrowheadException extends RuntimeException {
	
	//=================================================================================================
	// members

	private ExceptionType exceptionType = ExceptionType.ARROWHEAD;
	private final String origin;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public ArrowheadException(final String msg, final String origin, final Throwable cause) {
	    super(msg, cause);
	    this.origin = origin;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ArrowheadException(final String msg, final String origin) {
	    super(msg);
	    this.origin = origin;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ArrowheadException(final String msg, final Throwable cause) {
	    super(msg, cause);
	    this.origin = null;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ArrowheadException(final String msg) {
	    super(msg);
	    this.origin = null;
	}
	
	//-------------------------------------------------------------------------------------------------
	public ExceptionType getExceptionType() { return exceptionType; }
	public String getOrigin() { return origin; }
	
	//-------------------------------------------------------------------------------------------------
	protected void setExceptionType(final ExceptionType exceptionType) { this.exceptionType = exceptionType; }
}
