package eu.arrowhead.common.http.filter.authentication;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.service.normalization.NormalizationMode;
import eu.arrowhead.common.service.validation.name.SystemNameNormalizer;

public class SelfDeclaredFilterTest {

	//=================================================================================================
	// members

	private final SelfDeclaredFilter filter = new SelfDeclaredFilterTestHelper(); // this is the trick

	private SystemNameNormalizer systemNameNormalizer;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalMissingAuthHeader() {
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(new MockHttpServletRequest(), null, new FilterChainMock()));
		assertEquals("No authorization header has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalEmptyAuthHeader() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, " ");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("No authorization header has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalAuthHeaderTooShort() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "oneword");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Invalid authorization header", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalAuthHeaderTooLong() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "first second third");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Invalid authorization header", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalAuthHeaderInvalidSchema() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "first second");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Invalid authorization header", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalAuthHeaderContentTooShort() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer systemName");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Invalid authorization header", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalAuthHeaderContentTooLong() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer prefix//systemName//other");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Invalid authorization header", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalAuthHeaderContentInvalidPrefix() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer prefix//systemName");
		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));
		assertEquals("Invalid authorization header", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalOkSystem() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer SYSTEM//systemName");

		ReflectionTestUtils.setField(filter, "systemNameNormalizer", systemNameNormalizer);

		final Throwable ex = assertThrows(RuntimeException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));

		assertAll("doFilterInternal - ok (system)",
				() -> assertEquals("OK", ex.getMessage()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM)),
				() -> assertEquals("SystemName", request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)),
				() -> assertFalse((boolean) request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void doFilterInternalOkSysop() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer SYSTEM//sysop");

		ReflectionTestUtils.setField(filter, "systemNameNormalizer", systemNameNormalizer);

		final Throwable ex = assertThrows(RuntimeException.class,
				() -> filter.doFilterInternal(request, null, new FilterChainMock()));

		assertAll("doFilterInternal - ok (system)",
				() -> assertEquals("OK", ex.getMessage()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM)),
				() -> assertEquals("Sysop", request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString()),
				() -> assertNotNull(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)),
				() -> assertTrue((boolean) request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST)));
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@BeforeEach
	private void init() {
		if (systemNameNormalizer == null) {
			systemNameNormalizer = new SystemNameNormalizer();
			ReflectionTestUtils.setField(systemNameNormalizer, "normalizationMode", NormalizationMode.EXTENDED);
		}
	}
}