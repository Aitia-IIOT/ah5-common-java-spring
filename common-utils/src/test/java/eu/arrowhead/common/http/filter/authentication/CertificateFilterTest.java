package eu.arrowhead.common.http.filter.authentication;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.security.SecurityUtilities;

public class CertificateFilterTest {

	//=================================================================================================
	// members

	private final CertificateFilter filter = new CertificateFilterTestHelper(); // this is the trick

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalMissingCert() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/test/");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Unauthenticated access attempt: http://localhost/test", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalInvalidCertType() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("device.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "device.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");
		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Unauthorized access: http://localhost/test, invalid certificate type: DEVICE", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalNotInTheCloud() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");

		final Map<String, Object> context = Map.of(Constants.SERVER_COMMON_NAME, "serviceregistry.testcloud.aitia.arrowhead.eu");
		ReflectionTestUtils.setField(filter, "arrowheadContext", context);

		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Unauthorized access: http://localhost/test, from foreign cloud", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalOkSystem() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");

		final Map<String, Object> context = Map.of(Constants.SERVER_COMMON_NAME, "serviceregistry.rubin.aitia.arrowhead.eu");
		ReflectionTestUtils.setField(filter, "arrowheadContext", context);

		final Throwable ex = assertThrows(RuntimeException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));

		assertAll("doFilterInternal - ok (system)",
				() -> assertEquals("OK", ex.getMessage()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM)),
				() -> assertEquals("test", request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)),
				() -> assertFalse((boolean) request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalOkSysOp() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("management.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "management.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		request.setRequestURI("/test/");

		final Map<String, Object> context = Map.of(Constants.SERVER_COMMON_NAME, "serviceregistry.rubin.aitia.arrowhead.eu");
		ReflectionTestUtils.setField(filter, "arrowheadContext", context);

		final Throwable ex = assertThrows(RuntimeException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));

		assertAll("doFilterInternal - ok (sysop)",
				() -> assertEquals("OK", ex.getMessage()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM)),
				() -> assertEquals("management", request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)),
				() -> assertTrue((boolean) request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)));
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private KeyStore initializeTestKeyStore(final String fileName) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keystore = KeyStore.getInstance("pkcs12");
		try (InputStream input = new FileInputStream(ResourceUtils.getFile("src/test/resources/certs/" + fileName))) {
			keystore.load(input, "123456".toCharArray());
		}

		return keystore;
	}
}