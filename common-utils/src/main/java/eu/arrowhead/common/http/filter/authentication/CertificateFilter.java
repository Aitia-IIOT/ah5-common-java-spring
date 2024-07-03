package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import org.springframework.core.annotation.Order;

import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(15)
public class CertificateFilter extends ArrowheadFilter {
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		// TODO
		// get certificate from the standard named request attribute "jakarta.servlet.request.X509Certificate"
		// check if valid and in the cloud
		// get <system-name> from the CN
		// put <system-name> to request attribute "arrowhead.authenticated"
		chain.doFilter(request, response);
	}

}
