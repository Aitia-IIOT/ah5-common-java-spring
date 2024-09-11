package eu.arrowhead.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.exception.InvalidParameterException;
import jakarta.annotation.PostConstruct;

@Component
public class SSLProperties {

	//=================================================================================================
	// members

	@Value(Constants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;

	@Value(Constants.$KEYSTORE_TYPE_WD)
	private String keyStoreType;

	@Value(Constants.$KEYSTORE_PATH_WD)
	private Resource keyStore;

	@Value(Constants.$KEYSTORE_PASSWORD_WD)
	private String keyStorePassword;

	@Value(Constants.$KEY_PASSWORD_WD)
	private String keyPassword;

	@Value(Constants.$KEY_ALIAS_WD)
	private String keyAlias;

	@Value(Constants.$TRUSTSTORE_PATH_WD)
	private Resource trustStore;

	@Value(Constants.$TRUSTSTORE_PASSWORD_WD)
	private String trustStorePassword;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void validate() {
		if (sslEnabled) {
			if (Utilities.isEmpty(keyStoreType)) {
				throw new InvalidParameterException("keyStoreType is missing");
			}

			if (keyStore == null) {
				throw new InvalidParameterException("keyStorePath is missing");
			}

			if (Utilities.isEmpty(keyStorePassword)) {
				throw new InvalidParameterException("keyStorePassword is missing");
			}

			if (Utilities.isEmpty(keyPassword)) {
				throw new InvalidParameterException("keyPassword is missing");
			}

			if (Utilities.isEmpty(keyAlias)) {
				throw new InvalidParameterException("keyAlias is missing");
			}

			if (trustStore == null) {
				throw new InvalidParameterException("trustStorePath is missing");
			}

			if (Utilities.isEmpty(trustStorePassword)) {
				throw new InvalidParameterException("trustStorePassword is missing");
			}
		}
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public boolean isSslEnabled() {
		return sslEnabled;
	}

	//-------------------------------------------------------------------------------------------------
	public String getKeyStoreType() {
		return keyStoreType;
	}

	//-------------------------------------------------------------------------------------------------
	public Resource getKeyStore() {
		return keyStore;
	}

	//-------------------------------------------------------------------------------------------------
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	//-------------------------------------------------------------------------------------------------
	public String getKeyPassword() {
		return keyPassword;
	}

	public String getKeyAlias() {
		return keyAlias;
	}

	//-------------------------------------------------------------------------------------------------
	public Resource getTrustStore() {
		return trustStore;
	}

	//-------------------------------------------------------------------------------------------------
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
}