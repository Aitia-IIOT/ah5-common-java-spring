package eu.arrowhead.common.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.collector.ServiceCollector;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.ServiceModel;
import jakarta.annotation.PostConstruct;

@Service
public class ArrowheadHttpService {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private ServiceCollector collector;

	@Autowired
	private HttpService httpService;

	@Autowired
	private SystemInfo sysInfo;

	private String templateName;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public <T, P> T consumeService(final String serviceDefinition,
			final String operation,
			final Class<T> responseType,
			final P payload,
			final MultiValueMap<String, String> queryParams,
			final List<String> pathParams, // in order
			final Map<String, String> customHeaders) {
		logger.debug("consumeService started...");

		final ServiceModel model = collector.getServiceModel(serviceDefinition, templateName);
		final HttpInterfaceModel interfaceModel = (HttpInterfaceModel) model.interfaces().get(0);
		final HttpOperationModel operationModel = interfaceModel.operations().get(operation);
		final String authorizationHeader = calculateAuthorizationHeader();
		final Map<String, String> actualHeaders = new HashMap<>();
		if (customHeaders != null) {
			actualHeaders.putAll(customHeaders);
		}
		if (authorizationHeader != null) {
			actualHeaders.put(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		final String[] pathSegments = pathParams == null ? null : pathParams.toArray(String[]::new);
		final UriComponents uri = HttpUtilities.createURI(interfaceModel.protocol(), interfaceModel.accessAddresses().get(0), interfaceModel.accessPort(), queryParams,
				interfaceModel.basePath() + operationModel.path(), pathSegments);

		return httpService.sendRequest(uri, operationModel.method(), responseType, payload, null, actualHeaders);
	}

	//-------------------------------------------------------------------------------------------------
	public <T, P> T consumeService(final String serviceDefinition, final String operation, final Class<T> responseType, final P payload) {
		return consumeService(serviceDefinition, operation, responseType, payload, null, null, null);
	}

	//-------------------------------------------------------------------------------------------------
	public <T, P> T consumeService(final String serviceDefinition, final String operation, final Class<T> responseType, final MultiValueMap<String, String> queryParams) {
		return consumeService(serviceDefinition, operation, responseType, null, queryParams, null, null);
	}

	//-------------------------------------------------------------------------------------------------
	public <T> T consumeService(final String serviceDefinition, final String operation, final Class<T> responseType) {
		return consumeService(serviceDefinition, operation, responseType, null, null, null, null);
	}

	//-------------------------------------------------------------------------------------------------
	// path params in order
	public <T> T consumeService(final String serviceDefinition, final String operation, final Class<T> responseType, final List<String> pathParams) {
		return consumeService(serviceDefinition, operation, responseType, null, null, pathParams, null);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		templateName = sysInfo.isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;
	}

	//-------------------------------------------------------------------------------------------------
	private String calculateAuthorizationHeader() {
		logger.debug("calculateAuthorizationHeader started...");

		switch (sysInfo.getAuthenticationPolicy()) {
		case DECLARED:
			return Constants.HTTP_HEADER_AUTHORIZATION_SCHEMA + " " + Constants.HTTP_HEADER_AUTHORIZATION_PREFIX_SYSTEM + Constants.HTTP_HEADER_AUTHORIZATION_DELIMITER + sysInfo.getSystemName();
		case OUTSOURCED:
			final String identityToken = sysInfo.getIdentityToken();
			return identityToken == null ? null
					: Constants.HTTP_HEADER_AUTHORIZATION_SCHEMA + " " + Constants.HTTP_HEADER_AUTHORIZATION_PREFIX_IDENTITY_TOKEN + Constants.HTTP_HEADER_AUTHORIZATION_DELIMITER + identityToken;
		default:
			return null;
		}
	}
}