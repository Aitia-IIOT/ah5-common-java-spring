package eu.arrowhead.common.http.filter.authorization;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ConditionalOnProperty(name = Constants.ENABLE_MANAGEMENT_FILTER, matchIfMissing = false)
@Order(Constants.REQUEST_FILTER_ORDER_AUTHORIZATION_MGMT_SERVICE)
public class ManagementServiceFilter extends ArrowheadFilter {

	// TODO: if an endpoint is management, then check sysop, whitelist or authorization (depending on management.policy config)
	// requester comes from the request attribute "arrowhead.authenticated"

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {

//		Object attribute = request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM);

		super.doFilterInternal(request, response, chain);
	}
}
