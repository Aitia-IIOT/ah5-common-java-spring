package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import org.springframework.core.annotation.Order;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(Constants.REQUEST_FILTER_ORDER_AUTHENTICATION)
public class OutsourcedFilter extends ArrowheadFilter implements IAuthenticationPolicyFilter {

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		// TODO

		// get token from authorization header with the following format Bearer IDENTITY-TOKEN//<token> or Bearer AUTHENTICATOR-KEY//<system-name>//<secret-key>
		// check <token> with remote server or if it is the authenticator entity, then verify the secret key
		// get <system-name> from the response of the remote server or from the header in case of the authenticator entity
		// put <system-name> to request attribute "arrowhead.authenticated.system"

		// Exceptions:
		// - when requester calls for '/serviceregistry/service-discovery/lookup' with login-service(?) as service definition and without auth header
		chain.doFilter(request, response);

		// TODO: when we implement this we also have to implement a method that logins to the auth server and store the token in the Arrowhead context. This method should be called from the
		// ApplicationInitListener and from a quartz job (that automatically changes the token that will expire soon).
	}

}
