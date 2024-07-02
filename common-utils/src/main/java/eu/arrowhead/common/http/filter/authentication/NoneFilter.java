package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import org.springframework.core.annotation.Order;

import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(15)
public class NoneFilter extends ArrowheadFilter {
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		// TODO
		// get authorization header with the following format SYSTEM <system-name>
		// put <system-name> to request attribute "arrowhead.authenticated"
		super.doFilterInternal(request, response, chain);
	}
}
