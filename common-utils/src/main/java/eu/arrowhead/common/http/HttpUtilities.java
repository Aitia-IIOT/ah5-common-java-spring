package eu.arrowhead.common.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.exception.TimeoutException;
import eu.arrowhead.dto.ErrorMessageDTO;

public class HttpUtilities {
	
	//=================================================================================================
	// members
	
	private static final Logger logger = LogManager.getLogger(HttpUtilities.class);


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
	
	//-------------------------------------------------------------------------------------------------
	public static ArrowheadException createExceptionFromErrorMessageDTO(final ErrorMessageDTO dto) {
		Assert.notNull(dto, "Error message object is null.");
		Assert.notNull(dto.exceptionType(), "Exception type is null.");
		
		switch (dto.exceptionType()) {
	    case ARROWHEAD:
	    	return new ArrowheadException(dto.errorMessage(), dto.origin());
	    case AUTH:
	    	return new AuthException(dto.errorMessage(), dto.origin());
	    case FORBIDDEN:
	    	return new ForbiddenException(dto.errorMessage(), dto.origin());
	    case INVALID_PARAMETER:
	    	return new InvalidParameterException(dto.errorMessage(), dto.origin());
        case DATA_NOT_FOUND:
        	return new DataNotFoundException(dto.errorMessage(), dto.origin());
        case TIMEOUT:
        	return new TimeoutException(dto.errorMessage(), dto.origin());
        case EXTERNAL_SERVER_ERROR:
        	return new ExternalServerError(dto.errorMessage(), dto.origin());
        case INTERNAL_SERVER_ERROR:
        	return new InternalServerError(dto.errorMessage(), dto.origin());
	    default:
	    	logger.error("Unknown exception type: {}", dto.exceptionType());
	    	return new ArrowheadException(dto.errorMessage(), dto.origin());
        }
    }
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private HttpUtilities() {
		throw new UnsupportedOperationException();
	}
}
