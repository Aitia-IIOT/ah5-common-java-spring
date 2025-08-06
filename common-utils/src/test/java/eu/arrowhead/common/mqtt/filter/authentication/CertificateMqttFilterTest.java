/*******************************************************************************
 *
 * Copyright (c) 2025 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package eu.arrowhead.common.mqtt.filter.authentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.cert.X509Certificate;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.common.security.CertificateProfileType;
import eu.arrowhead.common.security.SecurityUtilities;
import eu.arrowhead.common.security.SecurityUtilities.CommonNameAndType;
import eu.arrowhead.common.service.validation.name.SystemNameNormalizer;
import eu.arrowhead.dto.MqttRequestTemplate;

@ExtendWith(MockitoExtension.class)
public class CertificateMqttFilterTest {

	//=================================================================================================
	// members

	private static final String validAuthKey = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlENGpDQ0FzcWdBd0lCQWdJVVdYQ0o4eldENzdHb2daY0Z4am9CbHZLSmV5RXdEUVlKS29aSWh2Y05BUUVMDQpCUUF3TmpFTE1Ba0dBMVVFQmhNQ2JHOHhKek"
			+ "FsQmdOVkJBTU1IbFJsYzNSRGJHOTFaQzVEYjIxd1lXNTVMbUZ5DQpjbTkzYUdWaFpDNWxkVEFlRncweU5UQTFNakV4TVRFd01UaGFGdzB6TlRBMU1qRXhNVEV3TVRoYU1Fd3hDekFKDQpCZ05WQkM0VEFuTjVNVDB3T3dZRFZRUURERF"
			+ "JEYjI1emRXMWxja0YxZEdodmNtbDZZWFJwYjI0dVZHVnpkRU5zDQpiM1ZrTGtOdmJYQmhibmt1WVhKeWIzZG9aV0ZrTG1WMU1JSUJJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUThBDQpNSUlCQ2dLQ0FRRUE3RDh6N2RvYzk1dlkwdUF"
			+ "4OEpYd3ZyY2wrUTdNeWtGb0ZJRjF0bjRmZXN2UElYbzVlQ0dEDQpTOEZDT05XMFM1aWdRK2wwMEdkTi9TbEUwbzg1bEkwOFR2ZXBHRWtUT3RtMUoraHNBSFJENjVPcFBUanpXRFZ6DQpQNCtHempaU1VKbDQxaUJEU1cxWUhnaUZHOFAy"
			+ "VHFhVHFOU2NyZkx0S3lla1N6eS9tMjR1aCt6WDV0ak5vSjRHDQpkU1VlVE50dEhVdUNIMzlNQnhFbzVFNktwekZHYkM0MTA1V0hJSDFNR1dvek9yWjNrN3VkdkNMYkNUdlo4UEZ0DQpiRE40WW1qaXIwUEUrNkUyTjRJK2thZ0wxUHkvR"
			+ "G1OcEt2TExJNm0rWVdKaDJFck9BYzU2VGhWdmJDRGVMT2loDQphY2IyNlk5SWNyZGExak9hMzAveEdzUzNDbUZMSXBaald3SURBUUFCbzRIUk1JSE9NQm9HQTFVZEVRUVRNQkdDDQpDV3h2WTJGc2FHOXpkSWNFZndBQUFUQUpCZ05WSF"
			+ "JNRUFqQUFNSGtHQTFVZEl3UnlNSENBRkZJYys1TlhpZldaDQp3N2xkUGl5OVFXVzJrRmJ3b1VLa1FEQStNUXN3Q1FZRFZRUXVFd0p2Y2pFUU1BNEdBMVVFQ2d3SFEyOXRjR0Z1DQplVEVkTUJzR0ExVUVBd3dVUTI5dGNHRnVlUzVoY25"
			+ "KdmQyaGxZV1F1WlhXQ0ZFWkYreWpYV3VFNHg5NVREbUZuDQpBWmJKaUdPaU1CMEdBMVVkSlFRV01CUUdDQ3NHQVFVRkJ3TUJCZ2dyQmdFRkJRY0RBakFMQmdOVkhROEVCQU1DDQpCYUF3RFFZSktvWklodmNOQVFFTEJRQURnZ0VCQUlh"
			+ "UTN4dGVRYWx1ak12QU1hVk96eVVjWGpIc04rdUVHbHVIDQpJQS9JZU9tWW1nOXJQMkJpaDRDNlVLc2U4SlRxLzdGa1p0Y1BYV1pIRDRWK2Y0b2hONDA3Nk5oS0g0WmdUdEc4DQpRUXlxYVhzY1BmY0xrZ21SN1pmRUpIWGd1OXMzeGdDb"
			+ "WhPcTBQWXI5YjNQcjFKZXRuYnpjUXdzTWh5UlZYbEZNDQozZVoyNmJsL3J0WU5IdDVmQitRbEljcEZsTmJwZWhHbnBWY2gwUjNXSHZLOGp0SzFVT1pCZ2ZpMzBVeXNwY2JYDQpVczMraEdabVk0QVB6OE5lTHlpTS96cXJVYTdBSWQ1L3"
			+ "RreHd5U0R0dXF1V1pudElrTndNc1JWakxBQlhIODdCDQpJeEJGajJXb3U3c2hFVjQrQWRtR05ENlBML25UWkt1YUNEbXRjUU84aDFIcElGUkxiN2M9DQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tDQo=";

	@InjectMocks
	private CertificateMqttFilter filter;

	@Mock
	private ApplicationContext appContext;

	@Mock
	private SystemNameNormalizer systemNameNormalizer;

	private MockedStatic<SecurityUtilities> mockStatic;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@BeforeEach
	public void setUp() {
		mockStatic = mockStatic(SecurityUtilities.class);
	}

	//-------------------------------------------------------------------------------------------------
	@AfterEach
	public void tearDown() {
		mockStatic.close();
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MagicNumber")
	@Test
	public void testOrder() {
		assertEquals(15, filter.order());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyNull() {
		final String authKey = null;
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", authKey, "response", 0, null, "payload"));

		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilter(authKey, request));

		assertEquals("No authentication key has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyEmpty() {
		final String authKey = "";
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", authKey, "response", 0, null, "payload"));

		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilter(authKey, request));

		assertEquals("No authentication key has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyNotBase64() {
		final String authKey = "not_base64";
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", authKey, "response", 0, null, "payload"));

		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilter(authKey, request));

		assertEquals("Invalid authentication key has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyNotPEM() {
		final String authKey = "bm90X3BlbQ==";
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", authKey, "response", 0, null, "payload"));

		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilter(authKey, request));

		assertEquals("Invalid authentication key has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyNotCert() {
		final String authKey = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCmJtOTBYMkZmWTJWeWRHbG1hV05oZEdVPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0t";
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", authKey, "response", 0, null, "payload"));

		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilter(authKey, request));

		assertEquals("Invalid authentication key has been provided", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyNoRequesterData() {
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", validAuthKey, "response", 0, null, "payload"));

		mockStatic.when(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class))).thenReturn(null);

		final Throwable ex = assertThrows(AuthException.class,
				() -> filter.doFilter(validAuthKey, request));

		mockStatic.verify(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class)));

		assertEquals("Unauthenticated access attempt: test/", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyWrongCertificateProfile() {
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", validAuthKey, "response", 0, null, "payload"));

		mockStatic.when(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class))).thenReturn(new CommonNameAndType("cn", CertificateProfileType.ORGANIZATION));

		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> filter.doFilter(validAuthKey, request));

		mockStatic.verify(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class)));

		assertTrue(ex.getMessage().startsWith("Unauthorized access: "));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterAuthKeyOtherCloud() {
		final String requesterCN = "ConsumerAuthorization.TestCloud.Aitia.arrowhead.eu";
		final String serverCN = "ServiceRegistry.Rubin.Aitia.arrowhead.eu";
		ReflectionTestUtils.setField(filter, "arrowheadContext", Map.of("server.common.name", serverCN));
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", validAuthKey, "response", 0, null, "payload"));

		mockStatic.when(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class))).thenReturn(new CommonNameAndType(
				requesterCN,
				CertificateProfileType.SYSTEM));
		mockStatic.when(() -> SecurityUtilities.getCloudCN(serverCN)).thenCallRealMethod();
		mockStatic.when(() -> SecurityUtilities.isClientInTheLocalCloudByCNs(appContext, requesterCN, "Rubin.Aitia.arrowhead.eu")).thenReturn(false);

		final Throwable ex = assertThrows(ForbiddenException.class,
				() -> filter.doFilter(validAuthKey, request));

		mockStatic.verify(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class)));
		mockStatic.verify(() -> SecurityUtilities.getCloudCN(serverCN));
		mockStatic.verify(() -> SecurityUtilities.isClientInTheLocalCloudByCNs(appContext, requesterCN, "Rubin.Aitia.arrowhead.eu"));

		assertTrue(ex.getMessage().startsWith("Unauthorized access: "));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterSysopOk() {
		final String requesterCN = "Sysop.TestCloud.Aitia.arrowhead.eu";
		final String serverCN = "ServiceRegistry.TestCloud.Aitia.arrowhead.eu";
		ReflectionTestUtils.setField(filter, "arrowheadContext", Map.of("server.common.name", serverCN));
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", validAuthKey, "response", 0, null, "payload"));

		mockStatic.when(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class))).thenReturn(new CommonNameAndType(
				requesterCN,
				CertificateProfileType.OPERATOR));
		mockStatic.when(() -> SecurityUtilities.getCloudCN(serverCN)).thenCallRealMethod();
		mockStatic.when(() -> SecurityUtilities.isClientInTheLocalCloudByCNs(appContext, requesterCN, "TestCloud.Aitia.arrowhead.eu")).thenReturn(true);
		mockStatic.when(() -> SecurityUtilities.getClientNameFromClientCN(requesterCN)).thenCallRealMethod();
		when(systemNameNormalizer.normalize("Sysop")).thenReturn("Sysop");

		assertDoesNotThrow(() -> filter.doFilter(validAuthKey, request));

		mockStatic.verify(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class)));
		mockStatic.verify(() -> SecurityUtilities.getCloudCN(serverCN));
		mockStatic.verify(() -> SecurityUtilities.isClientInTheLocalCloudByCNs(appContext, requesterCN, "TestCloud.Aitia.arrowhead.eu"));
		mockStatic.verify(() -> SecurityUtilities.getClientNameFromClientCN(requesterCN));
		verify(systemNameNormalizer).normalize("Sysop");

		assertEquals("Sysop", request.getRequester());
		assertTrue(request.isSysOp());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testDoFilterSystemOk() {
		final String requesterCN = "ConsumerAuthorization.TestCloud.Aitia.arrowhead.eu";
		final String serverCN = "ServiceRegistry.TestCloud.Aitia.arrowhead.eu";
		ReflectionTestUtils.setField(filter, "arrowheadContext", Map.of("server.common.name", serverCN));
		final MqttRequestModel request = new MqttRequestModel("test/", "test-operation", new MqttRequestTemplate("trace", validAuthKey, "response", 0, null, "payload"));

		mockStatic.when(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class))).thenReturn(new CommonNameAndType(
				requesterCN,
				CertificateProfileType.SYSTEM));
		mockStatic.when(() -> SecurityUtilities.getCloudCN(serverCN)).thenCallRealMethod();
		mockStatic.when(() -> SecurityUtilities.isClientInTheLocalCloudByCNs(appContext, requesterCN, "TestCloud.Aitia.arrowhead.eu")).thenReturn(true);
		mockStatic.when(() -> SecurityUtilities.getClientNameFromClientCN(requesterCN)).thenCallRealMethod();
		when(systemNameNormalizer.normalize("ConsumerAuthorization")).thenReturn("ConsumerAuthorization");

		assertDoesNotThrow(() -> filter.doFilter(validAuthKey, request));

		mockStatic.verify(() -> SecurityUtilities.getIdentificationDataFromCertificate(any(X509Certificate.class)));
		mockStatic.verify(() -> SecurityUtilities.getCloudCN(serverCN));
		mockStatic.verify(() -> SecurityUtilities.isClientInTheLocalCloudByCNs(appContext, requesterCN, "TestCloud.Aitia.arrowhead.eu"));
		mockStatic.verify(() -> SecurityUtilities.getClientNameFromClientCN(requesterCN));
		verify(systemNameNormalizer).normalize("ConsumerAuthorization");

		assertEquals("ConsumerAuthorization", request.getRequester());
		assertFalse(request.isSysOp());
	}
}