package eu.arrowhead.common.http.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

public class NoOpArrowheadFilter extends ArrowheadFilter {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected boolean isActive() {
		return false;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		return true;
	}
}