package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import org.springframework.core.annotation.Order;

import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(15)
public class OutsourcedFilter extends ArrowheadFilter {

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		// TODO
		// get token from authorization header with the following format AUTHORIZATION_TOKEN <token>
		// check <token> with remote server
		// get <system-name> from the response of the remote server
		// put <system-name> to request attribute "arrowhead.authenticated"
		super.doFilterInternal(request, response, chain);
	}

}
