package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(Constants.REQUEST_FILTER_ORDER_AUTHENTICATION)
public class SelfDeclaredFilter extends ArrowheadFilter {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		try {
			initializeRequestAttributes(request);

			final String systemName = processAuthHeader(request);
			request.setAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM, systemName);
			request.setAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST, Constants.SYSOP.equals(systemName.toLowerCase().trim()));

			chain.doFilter(request, response);
		} catch (final ArrowheadException ex) {
			handleException(ex, response);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private String processAuthHeader(final HttpServletRequest request) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (Utilities.isEmpty(authHeader)) {
			throw new AuthException("No authorization header has been provided");
		}

		String[] split = authHeader.trim().split(" ");
		if (split.length != 2 || !split[0].equals(Constants.HTTP_HEADER_AUTHORIZATION_SCHEMA)) {
			throw new AuthException("Invalid authorization header");
		}

		split = split[1].split(Constants.HTTP_HEADER_AUTHORIZATION_DELIMITER);
		if (split.length != 2 || !split[0].equals(Constants.HTTP_HEADER_AUTHORIZATION_PREFIX_SYSTEM)) {
			throw new AuthException("Invalid authorization header");
		}

		return split[1];
	}
}
