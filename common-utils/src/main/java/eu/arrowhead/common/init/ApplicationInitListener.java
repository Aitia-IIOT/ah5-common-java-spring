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
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

public abstract class ApplicationInitListener {

	//=================================================================================================
	// members
	
	protected final Logger logger = LogManager.getLogger(getClass());
	
	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	protected Map<String,Object> arrowheadContext;
	
	@Autowired
	protected SSLProperties sslProperties;
	
	protected boolean standaloneMode = false;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@EventListener
	@Order(10)
	public void onApplicationEvent(final ContextRefreshedEvent event) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InterruptedException {
		logger.debug("Initialization in onApplicationEvent()...");
		
		//TODO: print system's name
		logger.info("SSL mode: {}", getSSLString());
		//TODO: print authentication method
		
		if (sslProperties.isSslEnabled()) {
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
	protected void customInit(final ContextRefreshedEvent event) throws InterruptedException {}
	
	//-------------------------------------------------------------------------------------------------
	protected void customDestroy() {}
	
	//-------------------------------------------------------------------------------------------------
	protected String getSSLString() {
		return sslProperties.isSslEnabled() ? "ENABLED" : "DISABLED";
	}
	
	//-------------------------------------------------------------------------------------------------
	private KeyStore initializeKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		logger.debug("initializeKeyStore started...");
		Assert.isTrue(sslProperties.isSslEnabled(), "SSL is not enabled.");
		final String messageNotDefined = " is not defined.";
		Assert.isTrue(!Utilities.isEmpty(sslProperties.getKeyStoreType()), Constants.KEYSTORE_TYPE + messageNotDefined);
		Assert.notNull(sslProperties.getKeyStore(), Constants.KEYSTORE_PATH + messageNotDefined);
		Assert.isTrue(sslProperties.getKeyStore().exists(), Constants.KEYSTORE_PATH + " file is not found.");
		Assert.notNull(sslProperties.getKeyStorePassword(), Constants.KEYSTORE_PASSWORD + messageNotDefined);
		
		final KeyStore keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
		keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());

		return keystore;
	}
	
	//-------------------------------------------------------------------------------------------------
	private void checkServerCertificate(KeyStore keyStore) {
		// TODO implement
	}
	
	//-------------------------------------------------------------------------------------------------
	private void obtainKeys(final KeyStore keyStore) {
		// TODO implement
	}
	
	//-------------------------------------------------------------------------------------------------
	private void registerToServiceRegistry() {
		// TODO implement
	}

}