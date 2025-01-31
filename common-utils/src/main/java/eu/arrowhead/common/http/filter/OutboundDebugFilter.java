package eu.arrowhead.common.http.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.web.util.ContentCachingResponseWrapper;

import eu.arrowhead.common.Utilities;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OutboundDebugFilter extends ArrowheadFilter {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		log.trace("Entering OutboundDebugFilter...");

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (response.getCharacterEncoding() == null) {
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		}

		final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
		try {
			chain.doFilter(httpRequest, responseWrapper);
		} finally {
			log.debug("Response to the {} request at: {}", httpRequest.getMethod(), httpRequest.getRequestURL().toString());
			if (responseWrapper.getContentSize() > 0) {
				final String body = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());
				log.debug("Body: {}", Utilities.toPrettyJson(body));
			}
			if (!responseWrapper.isCommitted()) {
				responseWrapper.copyBodyToResponse();
			}
		}
	}
}