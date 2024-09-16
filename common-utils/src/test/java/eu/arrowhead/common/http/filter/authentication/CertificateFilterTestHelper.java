package eu.arrowhead.common.http.filter.authentication;

import java.io.IOException;

import eu.arrowhead.common.exception.ArrowheadException;
import jakarta.servlet.ServletResponse;

// we test this derived class instead of the original one
public class CertificateFilterTestHelper extends CertificateFilter {

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	// this method just re-throw the input exception which is easier to test than intercept the error response somehow
	@Override
	protected void handleException(final ArrowheadException ex, final ServletResponse response) throws IOException {
		throw ex;
	}
}