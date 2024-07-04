package eu.arrowhead.common.http;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.exception.TimeoutException;
import eu.arrowhead.dto.ErrorMessageDTO;

public final class HttpUtilities {

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

	//-------------------------------------------------------------------------------------------------
	/**
	 * @param scheme       default: http
	 * @param host         default: 127.0.0.1
	 * @param port         default: 80
	 * @param queryParams  default: null
	 * @param path         default: null
	 * @param pathSegments default: null
	 */
	public static UriComponents createURI(final String scheme, final String host, final int port, final MultiValueMap<String, String> queryParams, final String path, final String... pathSegments) {
		final UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
		builder.scheme(Utilities.isEmpty(scheme) ? Constants.HTTP : scheme.trim())
				.host(Utilities.isEmpty(host) ? Constants.LOCALHOST : host.trim())
				.port(port <= 0 ? Constants.HTTP_PORT : port);

		if (queryParams != null) {
			builder.queryParams(queryParams);
		}

		if (pathSegments != null && pathSegments.length > 0) {
			builder.pathSegment(pathSegments);
		}

		if (!Utilities.isEmpty(path)) {
			builder.path(path);
		}

		return builder.build();
	}

	//-------------------------------------------------------------------------------------------------
	public static UriComponents createURI(final String scheme, final String host, final int port, final String path) {
		return createURI(scheme, host, port, null, path, (String[]) null);
	}

	//-------------------------------------------------------------------------------------------------
	public static UriComponents createURI(final String scheme, final String host, final int port, final String path, final String... queryParams) {
		if (queryParams == null || queryParams.length == 0) {
			return createURI(scheme, host, port, path);
		}
		if (queryParams.length % 2 != 0) {
			throw new InvalidParameterException("queryParams variable arguments conatins a key without value");
		}

		final LinkedMultiValueMap<String, String> query = new LinkedMultiValueMap<>();

		int count = 1;
		String key = "";
		for (final String vararg : queryParams) {
			if (count % 2 != 0) {
				query.putIfAbsent(vararg, new ArrayList<>());
				key = vararg;
			} else {
				query.get(key).add(vararg);
			}
			count++;
		}

		return createURI(scheme, host, port, query, path);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private HttpUtilities() {
		throw new UnsupportedOperationException();
	}
}