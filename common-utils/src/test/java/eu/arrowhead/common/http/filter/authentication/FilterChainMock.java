package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class FilterChainMock implements FilterChain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// this method just need to stop execution in case of everything is working
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
		throw new RuntimeException("OK");
	}
}
