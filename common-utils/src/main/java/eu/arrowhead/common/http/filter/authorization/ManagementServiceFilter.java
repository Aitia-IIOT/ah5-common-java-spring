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
@Order(20)
public class ManagementServiceFilter extends ArrowheadFilter {

	// TODO: if an endpoint is management, then check auth rules or if sysop
	// requester comes from the request attribute "arrowhead.authenticated"
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		// TODO
		// get authorization header with the following format SYSTEM <system-name>
		// put <system-name> to request attribute "arrowhead.authenticated"
		
		Object attribute = request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM);
		System.out.println(attribute);
		
		super.doFilterInternal(request, response, chain);
	}
}
