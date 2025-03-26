package eu.arrowhead.common.mqtt.filter.authentication;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.common.security.SecurityUtilities;
import eu.arrowhead.common.service.validation.name.NameNormalizer;
import eu.arrowhead.dto.IdentityVerifyResponseDTO;
import eu.arrowhead.dto.ServiceInstanceLookupRequestDTO;
import jakarta.annotation.PostConstruct;

public class OutsourcedMqttFilter implements ArrowheadMqttFilter {

	//=================================================================================================
	// members

	private static final String AUTH_KEY_DELIMITER = Constants.HTTP_HEADER_AUTHORIZATION_DELIMITER;
	private static final String AUTH_KEY_PREFIX_AUTHENTICATOR_KEY = Constants.HTTP_HEADER_AUTHORIZATION_PREFIX_AUTHENTICATOR_KEY;
	private static final String AUTH_KEY_PREFIX_IDENTITY_TOKEN = Constants.HTTP_HEADER_AUTHORIZATION_PREFIX_IDENTITY_TOKEN;

	private final Logger log = LogManager.getLogger(getClass());

	@Value(Constants.$AUTHENTICATOR_SECRET_KEYS)
	private Map<String, String> rawSecretKeys;

	private Map<String, String> secretKeys = null;

	@Autowired
	private SystemInfo sysInfo;

	@Autowired
	private NameNormalizer nameNormalizer;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ArrowheadHttpService httpService;

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
		log.debug("Checking access in OutsourcedMqttFilter...");

		initializeRequestAttributes(request);

		// if request is for lookup for authentication's identity service, no need for check
		final boolean isAuthenticationLookup = isAuthenticationLookup(request);

		if (!isAuthenticationLookup) {
			final AuthenticationData data = processAuthKey(authKey);
			request.setRequester(data.systemName);
			request.setSysOp(data.sysop());
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void initializeRequestAttributes(final MqttRequestModel request) {
		request.setRequester(null);
		request.setSysOp(false);
	}

	//-------------------------------------------------------------------------------------------------
	private boolean isAuthenticationLookup(final MqttRequestModel request) {
		log.debug("OutsourcedMqttFilter.isAuthenticationLookup started...");

		// is the filter running inside Service Registry?
		if (!sysInfo.getSystemName().equals(Constants.SYS_NAME_SERVICE_REGISTRY)) {
			return false;
		}

		// is the requester wants to do service lookup?
		if (!Constants.SERVICE_OP_LOOKUP.equalsIgnoreCase(request.getOperation())
				|| !request.getBaseTopic().contains(Constants.SERVICE_DEF_SERVICE_DISCOVERY)) {
			return false;
		}

		// is the requester looking for the identity service definition?
		ServiceInstanceLookupRequestDTO dto = null; // expected type for service definition lookup
		try {
			// check if the content type can be mapped to the expected DTO
			dto = readPayload(request.getPayload(), ServiceInstanceLookupRequestDTO.class);
		} catch (final Exception ex) {
			return false;
		}

		if (dto == null || dto.serviceDefinitionNames() == null || dto.serviceDefinitionNames().size() != 1 || !dto.serviceDefinitionNames().getFirst().equals(Constants.SERVICE_DEF_IDENTITY)) {
			// dto is null or the requester is not (only) looking for the identity
			return false;
		}

		return true;
	}

	//-------------------------------------------------------------------------------------------------
	private <T> T readPayload(final Object payload, final Class<T> dtoClass) {
		log.debug("OutsourcedMqttFilter.readPayload started...");

		if (payload == null) {
			return null;
		}

		if (dtoClass.isInstance(payload)) {
			return dtoClass.cast(payload);
		}

		try {
			return mapper.readValue(mapper.writeValueAsString(payload), dtoClass);
		} catch (final IOException ex) {
			throw new InvalidParameterException("Could not parse payload. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private AuthenticationData processAuthKey(final String authKey) {
		log.debug("OutsourcedMqttFilter.processAuthKey started...");

		if (Utilities.isEmpty(authKey)) {
			throw new AuthException("No authentication info has been provided");
		}

		final String[] split = authKey.split(AUTH_KEY_DELIMITER);
		if (split[0].equals(AUTH_KEY_PREFIX_AUTHENTICATOR_KEY)) {
			return checkAuthenticaticatorKey(split);
		}

		if (split[0].equals(AUTH_KEY_PREFIX_IDENTITY_TOKEN)) {
			return checkIdentityToken(split);
		}

		throw new AuthException("Invalid authentication info");
	}

	//-------------------------------------------------------------------------------------------------
	// handling header using IDENTITY-TOKEN//<token> format
	private AuthenticationData checkIdentityToken(final String[] infoParts) {
		log.debug("OutsourcedMqttFilter.checkIdentityToken started...");

		if (infoParts.length != 2) {
			throw new AuthException("Invalid authentication info");
		}

		final String token = infoParts[1].trim();
		final IdentityVerifyResponseDTO response = httpService.consumeService(Constants.SERVICE_DEF_IDENTITY, Constants.SERVICE_OP_IDENTITY_VERIFY, IdentityVerifyResponseDTO.class, List.of(token));
		if (response.verified()) {
			return new AuthenticationData(
					response.systemName(),
					response.sysop());
		}

		throw new AuthException("Invalid authentication info");
	}

	//-------------------------------------------------------------------------------------------------
	// handling header using AUTHENTICATOR-KEY//<system-name>//<hash>
	@SuppressWarnings("checkstyle:MagicNumber")
	private AuthenticationData checkAuthenticaticatorKey(final String[] infoParts) {
		log.debug("OutsourcedMqttFilter.checkAuthenticaticatorKey started...");

		if (Utilities.isEmpty(secretKeys)) {
			// this system does not support authenticator keys
			throw new AuthException("Invalid authentication info");
		}

		if (infoParts.length != 3) {
			throw new AuthException("Invalid authentication info");
		}

		final String systemName = nameNormalizer.normalize(infoParts[1]);
		final String hash = infoParts[2].trim();

		if (!secretKeys.containsKey(systemName)) {
			// requester system is unknown
			throw new AuthException("Invalid authentication info");
		}

		try {
			final String calculatedHash = SecurityUtilities.hashWithSecretKey(systemName, secretKeys.get(systemName));
			if (!hash.equals(calculatedHash)) {
				// secret keys aren't the same
				throw new AuthException("Invalid authentication info");
			}
		} catch (final InvalidKeyException | NoSuchAlgorithmException ex) {
			// should never happen
			log.error(ex.getMessage());
			log.debug(ex);

			// can't authenticate
			throw new AuthException("Invalid authentication info");
		}

		// an authentication system cannot be sysop
		return new AuthenticationData(systemName, false);
	}

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		if (!Utilities.isEmpty(rawSecretKeys)) {
			log.info("Authentication keys are supported.");

			secretKeys = new ConcurrentHashMap<>(rawSecretKeys.size());
			rawSecretKeys.forEach((name, secretKey) -> secretKeys.put(nameNormalizer.normalize(name), secretKey.trim()));
		}
	}

	//=================================================================================================
	// nested structures

	//-------------------------------------------------------------------------------------------------
	private record AuthenticationData(
			String systemName,
			boolean sysop) {
	}
}