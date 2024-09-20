package eu.arrowhead.common.init;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.ServiceConfigurationError;

import javax.security.auth.x500.X500Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.security.CertificateProfileType;
import eu.arrowhead.common.security.SecurityUtilities;
import eu.arrowhead.common.security.SecurityUtilities.CommonNameAndType;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

public abstract class ApplicationInitListener {

	//=================================================================================================
	// members

	protected final Logger logger = LogManager.getLogger(getClass());

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	protected Map<String, Object> arrowheadContext;

	@Autowired
	protected SystemInfo sysInfo;

	protected boolean standaloneMode = false;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@EventListener
	@Order(10)
	public void onApplicationEvent(final ContextRefreshedEvent event) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InterruptedException {
		logger.debug("Initialization in onApplicationEvent()...");

		logger.info("System name: {}", sysInfo.getSystemName());
		logger.info("SSL mode: {}", getSSLString());
		logger.info("Authentication policy: {}", sysInfo.getAuthenticationPolicy().name());

		if (sysInfo.isSslEnabled()) {
			final KeyStore keyStore = initializeKeyStore();
			obtainKeys(keyStore);
			if (sysInfo.getAuthenticationPolicy() == AuthenticationPolicy.CERTIFICATE) {
				// in this case the certificate must be compliant with the Arrowhead Certificate Structure
				checkServerCertificate(keyStore);
			}
		}

		registerToServiceRegistry();

		customInit(event);

		logger.debug("Initialization in onApplicationEvent() is done.");
	}

	//-------------------------------------------------------------------------------------------------
	@PreDestroy
	public void destroy() throws InterruptedException {
		logger.debug("destroy called...");

		// TODO: revoke services

		try {
			customDestroy();
		} catch (final Throwable t) {
			logger.error(t.getMessage());
			logger.debug(t);
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException {
	}

	//-------------------------------------------------------------------------------------------------
	protected void customDestroy() {
	}

	//-------------------------------------------------------------------------------------------------
	protected String getSSLString() {
		return sysInfo.isSslEnabled() ? "ENABLED" : "DISABLED";
	}

	//-------------------------------------------------------------------------------------------------
	private KeyStore initializeKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		logger.debug("initializeKeyStore started...");
		Assert.isTrue(sysInfo.isSslEnabled(), "SSL is not enabled.");
		final String messageNotDefined = " is not defined.";
		Assert.isTrue(!Utilities.isEmpty(sysInfo.getSslProperties().getKeyStoreType()), Constants.KEYSTORE_TYPE + messageNotDefined);
		Assert.notNull(sysInfo.getSslProperties().getKeyStore(), Constants.KEYSTORE_PATH + messageNotDefined);
		Assert.isTrue(sysInfo.getSslProperties().getKeyStore().exists(), Constants.KEYSTORE_PATH + " file is not found.");
		Assert.notNull(sysInfo.getSslProperties().getKeyStorePassword(), Constants.KEYSTORE_PASSWORD + messageNotDefined);

		final KeyStore keystore = KeyStore.getInstance(sysInfo.getSslProperties().getKeyStoreType());
		keystore.load(sysInfo.getSslProperties().getKeyStore().getInputStream(), sysInfo.getSslProperties().getKeyStorePassword().toCharArray());

		return keystore;
	}

	//-------------------------------------------------------------------------------------------------
	private void checkServerCertificate(final KeyStore keyStore) {
		logger.debug("checkServerCertificate started...");
		final X509Certificate serverCertificate = (X509Certificate) arrowheadContext.get(Constants.SERVER_CERTIFICATE);
		final CommonNameAndType serverData = SecurityUtilities.getIdentificationDataFromSubjectDN(serverCertificate.getSubjectX500Principal().getName(X500Principal.RFC2253));

		if (serverData == null) {
			throw new AuthException("Server certificate is not compliant with the Arrowhead certificate structure, common name and profile type not found.");
		}
		if (CertificateProfileType.SYSTEM != serverData.profileType()) {
			throw new AuthException("Server certificate is not compliant with the Arrowhead certificate structure, invalid profile type: " + serverData.profileType());
		}

		if (!SecurityUtilities.isValidSystemCommonName(serverData.commonName())) {
			logger.error("Server CN ({}) is not compliant with the Arrowhead certificate structure, since it does not have 5 parts.", serverData.commonName());
			throw new AuthException("Server CN (" + serverData.commonName() + ") is not compliant with the Arrowhead certificate structure, since it does not have 5 parts.");
		}
		logger.info("Server CN: {}", serverData.commonName());

		arrowheadContext.put(Constants.SERVER_COMMON_NAME, serverData.commonName());
	}

	//-------------------------------------------------------------------------------------------------
	private void obtainKeys(final KeyStore keyStore) {
		logger.debug("obtainKeys started...");

		final X509Certificate serverCertificate = SecurityUtilities.getCertificateFromKeyStore(keyStore, sysInfo.getSslProperties().getKeyAlias());
		if (serverCertificate == null) {
			// never happens because checkServer
			throw new ServiceConfigurationError("Cannot find server certificate in the specified key store.");
		}

		arrowheadContext.put(Constants.SERVER_CERTIFICATE, serverCertificate);
		final PublicKey publicKey = serverCertificate.getPublicKey();
		arrowheadContext.put(Constants.SERVER_PUBLIC_KEY, publicKey);

		final PrivateKey privateKey = SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, sysInfo.getSslProperties().getKeyAlias(), sysInfo.getSslProperties().getKeyPassword());
		if (privateKey == null) {
			throw new ServiceConfigurationError("Cannot find private key in the specified key store.");
		}
		arrowheadContext.put(Constants.SERVER_PRIVATE_KEY, privateKey);
	}

	//-------------------------------------------------------------------------------------------------
	private void registerToServiceRegistry() {
		// TODO implement
		// register system and its services
	}

}