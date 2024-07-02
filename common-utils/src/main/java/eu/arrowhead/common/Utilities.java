package eu.arrowhead.common;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.dto.ErrorMessageDTO;

public class Utilities {
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static HttpStatus calculateHttpStatusFromArrowheadException(final ArrowheadException ex) {
		Assert.notNull(ex, "Exception is null.");

		HttpStatus status = HttpStatus.resolve(ex.getExceptionType().getErrorCode());
	    if (status == null) {
	    	switch (ex.getExceptionType()) {
	    	case AUTH:
	    		status = HttpStatus.UNAUTHORIZED;
			    break;
	        case FORBIDDEN:
	        	status = HttpStatus.FORBIDDEN;
	        	break;
	        case INVALID_PARAMETER:
	        	status = HttpStatus.BAD_REQUEST;
	          	break;
	        case DATA_NOT_FOUND:
	        	status = HttpStatus.NOT_FOUND;
	        	break;
	        case EXTERNAL_SERVER_ERROR:
	        	status = HttpStatus.SERVICE_UNAVAILABLE;
	        	break;
	        case TIMEOUT:
	        	status = HttpStatus.REQUEST_TIMEOUT;
	        	break;
	        default:
	    		status = HttpStatus.INTERNAL_SERVER_ERROR;
	    	}
	    }

		return status;
	}
	
	//-------------------------------------------------------------------------------------------------
	public static ErrorMessageDTO createErrorMessageDTO(final ArrowheadException ex) {
		return new ErrorMessageDTO(ex.getMessage(), calculateHttpStatusFromArrowheadException(ex).value(), ex.getExceptionType(), ex.getOrigin());	
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Utilities() {
		throw new UnsupportedOperationException();
	}
}
