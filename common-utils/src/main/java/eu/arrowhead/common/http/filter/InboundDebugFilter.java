package eu.arrowhead.common.http.filter;

import java.io.IOException;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.http.filter.thirdparty.MultiReadRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InboundDebugFilter extends ArrowheadFilter {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		log.trace("Entering InboundDebugFilter...");

		final MultiReadRequestWrapper requestWrapper = new MultiReadRequestWrapper((HttpServletRequest) request);

		log.debug("New {} request at: {}", requestWrapper.getMethod(), requestWrapper.getRequestURL().toString());
		if (!Utilities.isEmpty(requestWrapper.getQueryString())) {
			log.debug("Query string: {}", requestWrapper.getQueryString());
		}

		if (!Utilities.isEmpty(requestWrapper.getCachedBody())) {
			log.debug("Body: {}", Utilities.toPrettyJson(requestWrapper.getCachedBody()));
		}

		chain.doFilter(requestWrapper, response);
	}
}