package eu.arrowhead.common.mqtt.filter.authentication;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.common.security.CertificateProfileType;
import eu.arrowhead.common.security.SecurityUtilities;
import eu.arrowhead.common.security.SecurityUtilities.CommonNameAndType;
import jakarta.annotation.Resource;

public class CertificateMqttFilter implements ArrowheadMqttFilter {

	//=================================================================================================
	// members

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	private Map<String, Object> arrowheadContext;

	private static final String beginCert = "-----BEGIN CERTIFICATE-----";
	private static final String endCert = "-----END CERTIFICATE-----";
	private static final String whitespaceRegexp = "\\s+";

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.REQUEST_FILTER_ORDER_AUTHENTICATION;
	}

	//---------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authKey, final MqttRequestModel request) {
		logger.debug("Checking access in CertificateMqttFilter...");

		final X509Certificate x509Certificate = decodeAuthorizationKey(authKey);
		final CommonNameAndType requesterData = SecurityUtilities.getIdentificationDataFromCertificate(x509Certificate);
		if (requesterData == null) {
			logger.error("Unauthenticated access attempt: {}", request.getRequestTopic());
			throw new AuthException("Unauthenticated access attempt: " + request.getRequestTopic());
		}

		checkClientAuthorized(requesterData, request.getRequestTopic());
		fillRequestAttributes(request, requesterData);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private X509Certificate decodeAuthorizationKey(final String authKey) {
		logger.debug("CertificateMqttFilter.decodeAuthorizationKey started...");

		if (Utilities.isEmpty(authKey)) {
			throw new AuthException("No authentication key has been provided");
		}

		// Base64 encoded X.509 certificate PEM format is expected
		String decodedX509PEM = new String(Base64.getDecoder().decode(authKey));
		decodedX509PEM = decodedX509PEM.replace(beginCert, "").replace(endCert, "").replaceAll(whitespaceRegexp, "");

		final byte[] decodedX509RawContent = Base64.getDecoder().decode(decodedX509PEM);

		try {
			final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			final ByteArrayInputStream certStream = new ByteArrayInputStream(decodedX509RawContent);
			return (X509Certificate) certificateFactory.generateCertificate(certStream);

		} catch (final CertificateException ex) {
			logger.error(ex.getMessage());
			logger.debug(ex);
			throw new AuthException("Invalid authentication key has been provided");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void checkClientAuthorized(final CommonNameAndType requesterData, final String requestTarget) {
		logger.debug("CertificateMqttFilter.checkClientAuthenticated started...");

		if (CertificateProfileType.SYSTEM != requesterData.profileType() && CertificateProfileType.OPERATOR != requesterData.profileType()) {
			logger.error("Unauthorized access: {}, invalid certificate type: {}", requestTarget, requesterData.profileType());
			throw new ForbiddenException("Unauthorized access: " + requestTarget + ", invalid certificate type: " + requesterData.profileType());
		}

		final String serverCN = (String) arrowheadContext.get(Constants.SERVER_COMMON_NAME);
		final String cloudCN = SecurityUtilities.getCloudCN(serverCN);
		if (!SecurityUtilities.isClientInTheLocalCloudByCNs(requesterData.commonName(), cloudCN)) {
			logger.error("Unauthorized access: {}, from foreign cloud", requestTarget);
			throw new ForbiddenException("Unauthorized access: " + requestTarget + ", from foreign cloud");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void fillRequestAttributes(final MqttRequestModel request, final CommonNameAndType requesterData) {
		logger.debug("CertificateMqttFilter.checkClientAuthenticated started...");

		request.setSysOp(CertificateProfileType.OPERATOR == requesterData.profileType());
		request.setRequester(SecurityUtilities.getClientNameFromClientCN(requesterData.commonName()));
		System.out.println(SecurityUtilities.getClientNameFromClientCN(requesterData.commonName()));
	}
}