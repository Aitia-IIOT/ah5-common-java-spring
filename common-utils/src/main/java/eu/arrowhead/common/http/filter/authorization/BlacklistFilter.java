package eu.arrowhead.common.http.filter.authorization;

import java.io.IOException;

import org.springframework.core.annotation.Order;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// TODO: conditional on 'enable.blacklist.filter'
@Order(18)
public class BlacklistFilter extends ArrowheadFilter {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		try {

			Object systemName = request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM);

			// TODO this filter should lookup for the blacklist service and use it against to the system name
			// if system name is found on the blacklist throw ForbiddenException
			// if requester is sysop, no need for check
			// if request is for lookup for authentication, no need for check
			// blacklist entry can be temporary (with expiration)

			chain.doFilter(request, response);
		} catch (final ArrowheadException ex) {
			handleException(ex, response);
		}
	}
}
