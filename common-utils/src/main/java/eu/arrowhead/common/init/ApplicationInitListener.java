package eu.arrowhead.common.init;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

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
			checkServerCertificate(keyStore);
			obtainKeys(keyStore);
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
		// TODO implement
	}

	//-------------------------------------------------------------------------------------------------
	private void obtainKeys(final KeyStore keyStore) {
		// TODO implement
	}

	//-------------------------------------------------------------------------------------------------
	private void registerToServiceRegistry() {
		// TODO implement
		// register system and its services
	}

}