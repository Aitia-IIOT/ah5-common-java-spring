package eu.arrowhead.common.collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.HttpService;
import eu.arrowhead.common.http.HttpUtilities;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.intf.properties.PropertyValidatorType;
import eu.arrowhead.common.intf.properties.PropertyValidators;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.dto.ServiceInstanceInterfaceResponseDTO;
import eu.arrowhead.dto.ServiceInstanceListResponseDTO;
import eu.arrowhead.dto.ServiceInstanceLookupRequestDTO;
import eu.arrowhead.dto.ServiceInstanceResponseDTO;
import jakarta.annotation.Nullable;

public class HttpCollectorDriver implements ICollectorDriver {

	//=================================================================================================
	// members

	private static final String SR_LOOKUP_PATH = "/serviceregistry/service-discovery/lookup";

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value(Constants.$HTTP_COLLECTOR_MODE_WD)
	private HttpCollectorMode mode;

	@Autowired
	private HttpService httpService;

	@Autowired
	private SystemInfo sysInfo;

	@Autowired
	private PropertyValidators validators;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void init() throws ArrowheadException {
		logger.debug("HttpCollectorDriver.init started...");

		if (HttpCollectorMode.SR_AND_ORCH == mode) {
			// TODO: try to lookup orchestration service and cache it
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	@Nullable
	public ServiceModel acquireService(final String serviceDefinitionName, final String interfaceTemplateName) throws ArrowheadException {
		logger.debug("acquireService started...");
		Assert.isTrue(!Utilities.isEmpty(serviceDefinitionName), "service definition is empty");

		if (!Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME.equals(interfaceTemplateName)
				&& !Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME.equals(interfaceTemplateName)) {
			throw new InvalidParameterException("This collector only supports the following interfaces: "
					+ String.join(", ", Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME, Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME));
		}

		ServiceModel result = acquireServiceFromSR(serviceDefinitionName, interfaceTemplateName);
		if (result == null && HttpCollectorMode.SR_AND_ORCH == mode) {
			result = acquireServiceFromOrchestrator(serviceDefinitionName, interfaceTemplateName);

		}

		return result;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ServiceModel acquireServiceFromSR(final String serviceDefinitionName, final String interfaceTemplateName) {
		logger.debug("acquireServiceFromSR started...");

		final String scheme = sysInfo.getSslProperties().isSslEnabled() ? Constants.HTTPS : Constants.HTTP;
		final UriComponents uri = HttpUtilities.createURI(scheme, sysInfo.getServiceRegistryAddress(), sysInfo.getServiceRegistryPort(), SR_LOOKUP_PATH);

		final ServiceInstanceLookupRequestDTO payload = createRequestPayload(serviceDefinitionName, interfaceTemplateName);

		final String authorizationHeader = HttpUtilities.calculateAuthorizationHeader(sysInfo);
		final Map<String, String> headers = new HashMap<>();
		if (authorizationHeader != null) {
			headers.put(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		final ServiceInstanceListResponseDTO response = httpService.sendRequest(uri, HttpMethod.POST, ServiceInstanceListResponseDTO.class, payload, null, headers);
		return convertLookupResponse(response);
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceModel acquireServiceFromOrchestrator(final String serviceDefinitionName, final String interfaceTemplateName) {
		// TODO Auto-generated method stub
		// try to orchestrate the service (if no orchestration service is cached, it will lookup for it first)
		return null;
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceInstanceLookupRequestDTO createRequestPayload(final String serviceDefinitionName, final String interfaceTemplateName) {
		logger.debug("createRequestPayload started...");

		return new ServiceInstanceLookupRequestDTO.Builder()
				.serviceDefinitionName(serviceDefinitionName)
				.interfaceTemplateName(interfaceTemplateName)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private ServiceModel convertLookupResponse(final ServiceInstanceListResponseDTO response) {
		logger.debug("convertLookupResponse started...");

		if (response.entries().isEmpty()) {
			return null;
		}

		final ServiceInstanceResponseDTO instance = response.entries().get(0);
		final ServiceInstanceInterfaceResponseDTO intf = instance.interfaces().get(0);
		final Map<String, Object> intfProps = intf.properties(); // this should be contains accessAddresses, accessPort, basePath, and optionally operations
		final List<String> accessAddressess = (List<String>) intfProps.get(HttpInterfaceModel.PROP_NAME_ACCESS_ADDRESSES);
		final int accessPort = (int) intfProps.get(HttpInterfaceModel.PROP_NAME_ACCESS_PORT);
		final String basePath = (String) intfProps.get(HttpInterfaceModel.PROP_NAME_BASE_PATH);
		final Map<String, HttpOperationModel> operations = intfProps.containsKey(HttpInterfaceModel.PROP_NAME_OPERATIONS)
				? (Map<String, HttpOperationModel>) validators.getValidator(PropertyValidatorType.HTTP_OPERATIONS).validateAndNormalize(intfProps.get(HttpInterfaceModel.PROP_NAME_OPERATIONS))
				: Map.of();

		final InterfaceModel interfaceModel = new HttpInterfaceModel.Builder(intf.templateName())
				.accessAddresses(accessAddressess)
				.accessPort(accessPort)
				.basePath(basePath)
				.operations(operations)
				.build();

		return new ServiceModel.Builder()
				.serviceDefinition(instance.serviceDefinition().name())
				.version(instance.version())
				.metadata(instance.metadata())
				.serviceInterface(interfaceModel)
				.build();
	}
}