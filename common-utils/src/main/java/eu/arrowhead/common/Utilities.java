package eu.arrowhead.common;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.dto.ErrorMessageDTO;

public class Utilities {
	
	//=================================================================================================
	// members
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static boolean isEmpty(final String str) {
		return str == null || str.isBlank();
	}
	
	//-------------------------------------------------------------------------------------------------
	public static boolean isEmpty(final Map<?,?> map) {
		return map == null || map.isEmpty();
	}
	
	//-------------------------------------------------------------------------------------------------
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String toJson(final Object object) {
		if (object == null) {
			return null;
		}

		try {
			return mapper.writeValueAsString(object);
		} catch (final JsonProcessingException ex) {
			throw new ArrowheadException("The specified object cannot be converted to text.", ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static <T> T fromJson(final String json, final Class<T> parsedClass) {
		if (json == null || parsedClass == null) {
			return null;
		}

	    try {
	    	return mapper.readValue(json, parsedClass);
	    } catch (final IOException ex) {
	      throw new ArrowheadException("The specified string cannot be converted to a(n) " + parsedClass.getSimpleName() + " object.", ex);
	    }
	}
	
	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static <T> T fromJson(final String json, final com.fasterxml.jackson.core.type.TypeReference<T> reference) {
		if (json == null || reference == null) {
			return null;
		}

	    try {
	    	return mapper.readValue(json, reference);
	    } catch (final IOException ex) {
	      throw new ArrowheadException("The specified string cannot be converted to a(n) " + reference.getType() + " object.", ex);
	    }
	}
	
	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String toPrettyJson(final String jsonString) {
		try {
			if (jsonString != null) {
				final String jsonString_ = jsonString.trim();
				if (jsonString_.startsWith("{")) {
					final Object tempObj = mapper.readValue(jsonString_, Object.class);
					return mapper.writeValueAsString(tempObj);
				} else {
					final Object[] tempObj = mapper.readValue(jsonString_, Object[].class);
					return mapper.writeValueAsString(tempObj);
				}
			}
	    } catch (final IOException ex) {
	    	// it seems it is not a JSON string, so we just return untouched
	    }

	    return jsonString;
	}
	
	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String stripEndSlash(final String uri) {
	    if (uri != null && uri.endsWith("/")) {
	    	return uri.substring(0, uri.length() - 1);
	    }

	    return uri;
	}
	
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
