package eu.arrowhead.common.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import eu.arrowhead.common.mqtt.model.MqttInterfaceModel;
import eu.arrowhead.dto.ServiceInstanceInterfaceResponseDTO;
import eu.arrowhead.dto.ServiceInstanceListResponseDTO;
import eu.arrowhead.dto.ServiceInstanceLookupRequestDTO;
import eu.arrowhead.dto.ServiceInstanceResponseDTO;
import jakarta.annotation.Nullable;

public class HttpCollectorDriver implements ICollectorDriver {

	//=================================================================================================
	// members

	private static final String SR_LOOKUP_PATH = "/serviceregistry/service-discovery/lookup";
	private static final String VERBOSE_KEY = "verbose";
	private static final String VERBOSE_VALUE = "false";

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value(Constants.$HTTP_COLLECTOR_MODE_WD)
	private HttpCollectorMode mode;

	@Autowired
	private HttpService httpService;

	@Autowired
	private SystemInfo sysInfo;

	@Autowired
	private PropertyValidators validators;

	private final List<String> supportedInterfaces = List.of(
			Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME,
			Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME,
			Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME,
			Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME);

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

		if (!supportedInterfaces.contains(interfaceTemplateName)) {
			throw new InvalidParameterException("This collector only supports the following interfaces: " + String.join(", ", supportedInterfaces));
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
		final UriComponents uri = HttpUtilities.createURI(scheme, sysInfo.getServiceRegistryAddress(), sysInfo.getServiceRegistryPort(), SR_LOOKUP_PATH, VERBOSE_KEY, VERBOSE_VALUE);

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
		final List<ServiceInstanceInterfaceResponseDTO> interfaces = instance.interfaces();

		// create the list of interface models
		final List<InterfaceModel> interfaceModelList = new ArrayList<>();
		for (final ServiceInstanceInterfaceResponseDTO interf : interfaces) {

			final String templateName = interf.templateName();
			final Map<String, Object> properties = interf.properties();

			// HTTP or HTTPS
			if (templateName.equals(Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME) || templateName.equals(Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME)) {
				interfaceModelList.add(createHttpInterfaceModel(templateName, properties));
			}

			// MQTT or MQTTS
			if (templateName.equals(Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME) || templateName.equals(Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME)) {
				interfaceModelList.add(createMqttInterfaceModel(templateName, properties));
			}
		}

		return new ServiceModel.Builder()
				.serviceDefinition(instance.serviceDefinition().name())
				.version(instance.version())
				.metadata(instance.metadata())
				.serviceInterfaces(interfaceModelList)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private HttpInterfaceModel createHttpInterfaceModel(final String templateName, final Map<String, Object> properties) {

		// access addresses
		final List<String> accessAddresses = (List<String>) properties.get(HttpInterfaceModel.PROP_NAME_ACCESS_ADDRESSES);

		// access port
		final int accessPort = (int) properties.get(HttpInterfaceModel.PROP_NAME_ACCESS_PORT);

		// base path
		final String basePath = (String) properties.get(HttpInterfaceModel.PROP_NAME_BASE_PATH);

		// operations
		final Map<String, HttpOperationModel> operations = properties.containsKey(HttpInterfaceModel.PROP_NAME_OPERATIONS)
				? (Map<String, HttpOperationModel>) validators.getValidator(PropertyValidatorType.HTTP_OPERATIONS).validateAndNormalize(properties.get(HttpInterfaceModel.PROP_NAME_OPERATIONS))
				: Map.of();

		// create the interface model
		final HttpInterfaceModel model = new HttpInterfaceModel
				.Builder(templateName)
				.accessAddresses(accessAddresses)
				.accessPort(accessPort)
				.basePath(basePath)
				.operations(operations)
				.build();

		return model;
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private MqttInterfaceModel createMqttInterfaceModel(final String templateName, final Map<String, Object> properties) {

		// access addresses
		final List<String> accessAddresses = (List<String>) properties.get(MqttInterfaceModel.PROP_NAME_ACCESS_ADDRESSES);

		// access port
		final int accessPort = (int) properties.get(MqttInterfaceModel.PROP_NAME_ACCESS_PORT);

		// topic
		final String topic = (String) properties.get(MqttInterfaceModel.PROP_NAME_TOPIC);

		// operations
		final Set<String> operations = properties.containsKey(MqttInterfaceModel.PROP_NAME_OPERATIONS)
				? new HashSet<String>((Collection<? extends String>) properties.get(MqttInterfaceModel.PROP_NAME_OPERATIONS)) : Set.of();

		// create the interface model
		MqttInterfaceModel model = new MqttInterfaceModel
				.Builder(templateName)
				.accessAddresses(accessAddresses)
				.accessPort(accessPort)
				.topic(topic)
				.operations(operations)
				.build();

		return model;
	}
}