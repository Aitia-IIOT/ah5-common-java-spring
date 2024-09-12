package eu.arrowhead.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ServiceConfigurationError;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

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
		final KeyStore keyStore = initializeTestKeyStore();
		final X509Certificate cert = SecurityUtilities.getCertificateFromKeyStore(keyStore, "wrong");
		assertNull(cert);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getCertificateFromKeyStoreOk() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore();
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
		final KeyStore keyStore = initializeTestKeyStore();
		final PrivateKey key = SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, "wrong", "123456");
		assertNull(key);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreWrongPass() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore();
		final Throwable ex = assertThrows(ServiceConfigurationError.class, () -> {
			SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu", "pass");
		});
		assertEquals("Getting the private key from key store failed...", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getPrivateKeyFromKeyStoreOk() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keyStore = initializeTestKeyStore();
		final PrivateKey key = SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, "test.rubin.aitia.arrowhead.eu", "123456");
		assertNotNull(key);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private KeyStore initializeTestKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		final KeyStore keystore = KeyStore.getInstance("pkcs12");
		try (InputStream input = new FileInputStream(ResourceUtils.getFile("src/test/resources/certs/test.p12"))) {
			keystore.load(input, "123456".toCharArray());
		}

		return keystore;
	}
}