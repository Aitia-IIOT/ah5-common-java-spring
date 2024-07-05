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

		// get token from authorization header with the following format Bearer IDENTITY-TOKEN//<token> or Bearer AUTHENTICATOR-KEY//<system-name>//<secret-key>
		// check <token> with remote server or if it is the authenticator entity, then verify the secret key
		// get <system-name> from the response of the remote server or from the header in case of the authenticator entity
		// put <system-name> to request attribute "arrowhead.authenticated.system"

		// Exceptions:
		// - when requester calls for '/serviceregistry/api/service-discovery/lookup' with login-service(?) as service definition and without auth header
		chain.doFilter(request, response);
	}

}
