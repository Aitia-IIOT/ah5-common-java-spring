package eu.arrowhead.common.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HexFormat;
import java.util.ServiceConfigurationError;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.ResourceUtils;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.security.SecurityUtilities.CommonNameAndType;

public class SecurityUtilitiesTest {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreNullKeyStore() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getCertificateFromKeyStore(null, null);
		});
		assertEquals("Key store is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreNullAlias() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getCertificateFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), null);
		});
		assertEquals("Alias is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreEmptyAlias() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getCertificateFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), " ");
		});
		assertEquals("Alias is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreInvalidKeyStore() {
		final Throwable ex = assertThrows(ServiceConfigurationError.class, () -> {
			SecurityUtilities.getCertificateFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), "test");
		});
		assertEquals("Accessing certificate from key store failed...", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreInvalidAlias() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "wrong");
		assertNull(cert);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreOk() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu");
		assertNotNull(cert);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreNullKeyStore() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(null, null, null);
		});
		assertEquals("Key store is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreNullAlias() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), null, null);
		});
		assertEquals("Alias is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreEmptyAlias() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), " ", null);
		});
		assertEquals("Alias is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreNullKeyPass() {
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), "test", null);
		});
		assertEquals("Password is not defined.", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreInvalidKeyStore() {
		final Throwable ex = assertThrows(ServiceConfigurationError.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(KeyStore.getInstance(KeyStore.getDefaultType()), "test", "pass");
		});
		assertEquals("Getting the private key from key store failed...", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreInvalidAlias() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final PrivateKey key = SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, "wrong", "123456");
		assertNull(key);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreWrongPass() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final Throwable ex = assertThrows(ServiceConfigurationError.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu", "pass");
		});
		assertEquals("Getting the private key from key store failed...", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreOk() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final PrivateKey key = SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu", "123456");
		assertNotNull(key);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNNullDN() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN(null);
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNEmptyDN() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN(" ");
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNInvalidFormat() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN("blabla");
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNNoQualifier() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN("cn=test.rubin.aitia.arrowhead.eu");
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNInvalidQualifier() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN("cn=test.rubin.aitia.arrowhead.eu,2.5.4.46=#" + HexFormat.of().formatHex("wrong".getBytes()));
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNMissiongCN() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN("2.5.4.46=#" + HexFormat.of().formatHex("sy".getBytes()));
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromSubjectDNOk() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromSubjectDN("cn=test.rubin.aitia.arrowhead.eu,2.5.4.46=#" + HexFormat.of().formatHex("sy".getBytes())
				+ ",2.5.4.46=#" + HexFormat.of().formatHex("other".getBytes()));

		assertAll("Identification data - ok",
				() -> assertNotNull(data),
				() -> assertEquals("test.rubin.aitia.arrowhead.eu", data.commonName()),
				() -> assertEquals(CertificateProfileType.SYSTEM, data.profileType()));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromRequestNullRequest() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> SecurityUtilities.getIdentificationDataFromRequest(null));
		assertEquals("request must not be null", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromRequestNoCertificateChain() {
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromRequest(new MockHttpServletRequest());
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromRequestEmptyCertificateChain() {
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, new X509Certificate[0]);
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromRequest(request);
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromRequestInvalidCertificate() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("wrong.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "wrong.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromRequest(request);
		assertNull(data);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getIdentificationDataFromRequestOk() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore("test.p12");
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu");
		final X509Certificate[] certChain = new X509Certificate[1];
		certChain[0] = cert;
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE, certChain);
		final CommonNameAndType data = SecurityUtilities.getIdentificationDataFromRequest(request);

		assertAll("Identification data from request - ok",
				() -> assertNotNull(data),
				() -> assertEquals("test.rubin.aitia.arrowhead.eu", data.commonName()),
				() -> assertEquals(CertificateProfileType.SYSTEM, data.profileType()));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isValidSystemCommonNameNull() {
		final boolean valid = SecurityUtilities.isValidSystemCommonName(null);
		assertFalse(valid);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isValidSystemCommonNameEmpty() {
		final boolean valid = SecurityUtilities.isValidSystemCommonName("  ");
		assertFalse(valid);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isValidSystemCommonNameTooShort() {
		final boolean valid = SecurityUtilities.isValidSystemCommonName("rubin.aitia.arrowhead.eu");
		assertFalse(valid);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isValidSystemCommonNameTooLong() {
		final boolean valid = SecurityUtilities.isValidSystemCommonName("plusone.test.rubin.aitia.arrowhead.eu");
		assertFalse(valid);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isValidSystemCommonNameOk() {
		final boolean valid = SecurityUtilities.isValidSystemCommonName("test.rubin.aitia.arrowhead.eu");
		assertTrue(valid);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCloudCNInvalidFormat() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> SecurityUtilities.getCloudCN("not_a_cn"));
		assertEquals("Client common name is invalid: not_a_cn", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCloudCNOk() {
		final String cloudCN = SecurityUtilities.getCloudCN("test.rubin.aitia.arrowhead.eu");
		assertEquals("rubin.aitia.arrowhead.eu", cloudCN);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isClientInTheLocalCloudByCNsBasicTests() {
		assertAll("isClientInTheLocalCloudByCNs - basic",
				() -> assertFalse(SecurityUtilities.isClientInTheLocalCloudByCNs(null, null)),
				() -> assertFalse(SecurityUtilities.isClientInTheLocalCloudByCNs(" ", null)),
				() -> assertFalse(SecurityUtilities.isClientInTheLocalCloudByCNs("test.rubin.aitia.arrowhead.eu", null)),
				() -> assertFalse(SecurityUtilities.isClientInTheLocalCloudByCNs("test.rubin.aitia.arrowhead.eu", "")));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isClientInTheLocalCloudByCNsInvalidClientCN() {
		final boolean answer = SecurityUtilities.isClientInTheLocalCloudByCNs("test_rubin_aitia_arrowhead_eu", "rubin.aitia.arrowhead.eu");
		assertFalse(answer);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isClientInTheLocalCloudByCNsNo() {
		final boolean answer = SecurityUtilities.isClientInTheLocalCloudByCNs("test.rubin.aitia.arrowhead.eu", "testcloud.aitia.arrowhead.eu");
		assertFalse(answer);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isClientInTheLocalCloudByCNsYes() {
		final boolean answer = SecurityUtilities.isClientInTheLocalCloudByCNs("test.rubin.aitia.arrowhead.eu", "rubin.aitia.arrowhead.eu");
		assertTrue(answer);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getClientNameFromClientCNNull() {
		final String clientName = SecurityUtilities.getClientNameFromClientCN(null);
		assertNull(clientName);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getClientNameFromClientCNOk() {
		final String clientName = SecurityUtilities.getClientNameFromClientCN("test.rubin.aitia.arrowhead.eu");
		assertAll("getClientNameFromClientCN - ok",
				() -> assertNotNull(clientName),
				() -> assertEquals("test", clientName));
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