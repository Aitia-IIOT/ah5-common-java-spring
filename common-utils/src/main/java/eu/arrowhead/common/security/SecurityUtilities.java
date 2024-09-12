package eu.arrowhead.common.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

public final class SecurityUtilities {

	//=================================================================================================
	// members

	private static final String COMMON_NAME_FIELD_NAME = "CN";
	private static final String DN_QUALIFIER_FIELD_NAME = "2.5.4.46";
	private static final String X509_CN_DELIMITER = "\\.";
	private static final int SYSTEM_CN_NAME_LENGTH = 5;

	private static final Logger logger = LogManager.getLogger(SecurityUtilities.class);

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static X509Certificate getCertificateFromKeyStore(final KeyStore keystore, final String alias) {
		Assert.notNull(keystore, "Key store is not defined.");
		Assert.isTrue(!Utilities.isEmpty(alias), "Alias is not defined.");

		try {
			final Certificate[] chain = keystore.getCertificateChain(alias);
			return chain == null ? null : (X509Certificate) chain[0];
		} catch (final KeyStoreException ex) {
			logger.error("Accessing certificate from key store failed...", ex);
			throw new ServiceConfigurationError("Accessing certificate from key store failed...", ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static PrivateKey getPrivateKeyFromKeyStore(final KeyStore keystore, final String alias, final String keyPass) {
		Assert.notNull(keystore, "Key store is not defined.");
		Assert.isTrue(!Utilities.isEmpty(alias), "Alias is not defined.");
		Assert.notNull(keyPass, "Password is not defined.");

		try {
			return (PrivateKey) keystore.getKey(alias, keyPass.toCharArray());
		} catch (final KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException ex) {
			logger.error("Getting the private key from key store failed...", ex);
			throw new ServiceConfigurationError("Getting the private key from key store failed...", ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static CommonNameAndType getIdentificationDataFromCertificate(final String dn) {
		if (Utilities.isEmpty(dn)) {
			return null;
		}

		String commonName = null;
		List<String> dnQualifiers = new ArrayList<>();
		try {
			// DN is in LDAP format, we can use the LdapName object for parsing
			final LdapName ldapname = new LdapName(dn);
			for (final Rdn rdn : ldapname.getRdns()) {
				// Find the data after the CN and DN_QUALIFIER fields
				if (COMMON_NAME_FIELD_NAME.equalsIgnoreCase(rdn.getType())) {
					commonName = (String) rdn.getValue();
				} else if (DN_QUALIFIER_FIELD_NAME.equalsIgnoreCase(rdn.getType())) {
					final String dnQualifier = new String((byte[]) rdn.getValue(), StandardCharsets.UTF_8);
					dnQualifiers.add(dnQualifier.trim()); // important, there were white spaces in the test
				}
			}

			for (final String qualifier : dnQualifiers) {
				final CertificateProfileType type = CertificateProfileType.fromCode(qualifier);
				if (type != null) {
					return new CommonNameAndType(commonName, type);
				}
			}
		} catch (final InvalidNameException ex) {
			logger.warn("InvalidNameException in getIdentificationDataFromCertificate: {}", ex.getMessage());
			logger.debug("Exception", ex);
		}

		return null;
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static CommonNameAndType getIdentificationDataFromRequest(final HttpServletRequest request) {
		Assert.notNull(request, "request must not be null");
		final X509Certificate[] certificates = (X509Certificate[]) request.getAttribute(Constants.HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE);
		if (certificates != null && certificates.length > 0) {
			for (final X509Certificate cert : certificates) {
				final CommonNameAndType requesterData = getIdentificationDataFromCertificate(cert.getSubjectX500Principal().getName(X500Principal.RFC2253));
				if (requesterData == null || !isValidSystemCommonName(requesterData.commonName())) {
					continue;
				}

				return requesterData;
			}
		}

		return null;
	}

	//-------------------------------------------------------------------------------------------------
	public static boolean isValidSystemCommonName(final String commonName) {
		if (Utilities.isEmpty(commonName)) {
			return false;
		}

		final String[] cnFields = commonName.split(X509_CN_DELIMITER, 0);
		return cnFields.length == SYSTEM_CN_NAME_LENGTH;
	}

	//-------------------------------------------------------------------------------------------------
	public static String getCloudCN(final String systemCN) {
		final String[] fields = systemCN.split(X509_CN_DELIMITER, 2); // fields contains: systemName, <cloudName>.<organization>.<two parts of the master certificate, eg. arrowhead.eu>
		Assert.isTrue(fields.length >= 2, "System common name is invalid: " + systemCN);

		return fields[1];
	}

	//-------------------------------------------------------------------------------------------------
	public static boolean isClienInTheLocalCloudByCNs(final String clientCN, final String cloudCN) {
		if (Utilities.isEmpty(clientCN) || Utilities.isEmpty(cloudCN)) {
			return false;
		}

		final String[] fields = clientCN.split(X509_CN_DELIMITER, 2); // valid clientFields contains clientmName, <cloudName>.<organization>.<two parts of the master certificate, eg. arrowhead.eu>
		return fields.length >= 2 && cloudCN.equalsIgnoreCase(fields[1]);
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String getClientNameFromClientCN(final String clientCN) {
		if (clientCN == null) {
			return null;
		}

		return clientCN.split(X509_CN_DELIMITER, 2)[0];
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private SecurityUtilities() {
		throw new UnsupportedOperationException();
	}

	//=================================================================================================
	// nested structures

	//-------------------------------------------------------------------------------------------------
	public record CommonNameAndType(String commonName, CertificateProfileType profileType) {
	}
}