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
import eu.arrowhead.dto.OrchestrationRequestDTO;
import eu.arrowhead.dto.OrchestrationResponseDTO;
import eu.arrowhead.dto.OrchestrationResultDTO;
import eu.arrowhead.dto.OrchestrationServiceRequirementDTO;
import eu.arrowhead.dto.ServiceInstanceInterfaceResponseDTO;
import eu.arrowhead.dto.ServiceInstanceListResponseDTO;
import eu.arrowhead.dto.ServiceInstanceLookupRequestDTO;
import eu.arrowhead.dto.ServiceInstanceResponseDTO;
import eu.arrowhead.dto.enums.OrchestrationFlag;
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

	private ServiceModel orchestrationCache = null;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void init() throws ArrowheadException {
		logger.debug("HttpCollectorDriver.init started...");
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	@Nullable
	public ServiceModel acquireService(final String serviceDefinitionName, final String interfaceTemplateName, final String providerName) throws ArrowheadException {
		logger.debug("acquireService started...");
		Assert.isTrue(!Utilities.isEmpty(serviceDefinitionName), "service definition is empty");

		if (!supportedInterfaces.contains(interfaceTemplateName)) {
			throw new InvalidParameterException("This collector only supports the following interfaces: " + String.join(", ", supportedInterfaces));
		}

		ServiceModel result = acquireServiceFromSR(serviceDefinitionName, interfaceTemplateName, providerName);
		if (result == null && HttpCollectorMode.SR_AND_ORCH == mode) {
			result = acquireServiceFromOrchestration(serviceDefinitionName, interfaceTemplateName, providerName);
		}

		return result;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private ServiceModel acquireServiceFromSR(final String serviceDefinitionName, final String interfaceTemplateName, final String providerName) {
		logger.debug("acquireServiceFromSR started...");

		// 1. uri
		final String scheme = sysInfo.getSslProperties().isSslEnabled() ? Constants.HTTPS : Constants.HTTP;
		final UriComponents uri = HttpUtilities.createURI(scheme, sysInfo.getServiceRegistryAddress(), sysInfo.getServiceRegistryPort(), SR_LOOKUP_PATH, VERBOSE_KEY, VERBOSE_VALUE);

		// 2. payload
		final ServiceInstanceLookupRequestDTO payload = providerName == null ? createSRRequestPayload(serviceDefinitionName, interfaceTemplateName)
				: createSRRequestPayload(serviceDefinitionName, interfaceTemplateName, providerName);

		// 3. headers
		final String authorizationHeader = HttpUtilities.calculateAuthorizationHeader(sysInfo);
		final Map<String, String> headers = new HashMap<>();
		if (authorizationHeader != null) {
			headers.put(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		final ServiceInstanceListResponseDTO response = httpService.sendRequest(uri, HttpMethod.POST, ServiceInstanceListResponseDTO.class, payload, null, headers);

		return convertLookupResponse(response, interfaceTemplateName);
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceModel acquireServiceFromOrchestration(final String serviceDefinitionName, final String interfaceTemplateName, final String providerName) {
		logger.debug("acquireServiceFromOrchestration started...");

		// if no orchestration service is cached, it will lookup for it first
		if (orchestrationCache == null) {
			lookupAndCacheOrchestration();
			if (orchestrationCache == null) {
				logger.debug("Lookup and cache orchestration service was unsuccessful");
				return null;
			}
		}

		// orchestrate the service

		// 1. finding the corresponding interface
		final String intfTemplateNameToSendRequest = sysInfo.getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME
				: Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;
		final HttpInterfaceModel orchestrationIntf = (HttpInterfaceModel) orchestrationCache
				.interfaces()
				.stream()
				.filter(i -> i.templateName().equals(intfTemplateNameToSendRequest))
				.findFirst()
				.get();

		// 2. uri
		final String orchestrationPullPath = orchestrationIntf.operations().get(Constants.SERVICE_OP_ORCHESTRATION_PULL).path();
		final String scheme = sysInfo.getSslProperties().isSslEnabled() ? Constants.HTTPS : Constants.HTTP;
		final UriComponents uri = HttpUtilities.createURI(
				scheme,
				orchestrationIntf.accessAddresses().getFirst(),
				orchestrationIntf.accessPort(),
				orchestrationIntf.basePath() + orchestrationPullPath);

		// 3. method
		final HttpMethod orchestrationPullMethod = HttpMethod.valueOf(orchestrationIntf.operations().get(Constants.SERVICE_OP_ORCHESTRATION_PULL).method());

		// 4. payload
		final OrchestrationRequestDTO payload = providerName == null ? createOrchRequestPayload(serviceDefinitionName, interfaceTemplateName)
				: createOrchRequestPayload(serviceDefinitionName, interfaceTemplateName, providerName);

		// 5. headers
		final String authorizationHeader = HttpUtilities.calculateAuthorizationHeader(sysInfo);
		final Map<String, String> headers = new HashMap<>();
		if (authorizationHeader != null) {
			headers.put(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		final OrchestrationResponseDTO response = httpService.sendRequest(uri, orchestrationPullMethod, OrchestrationResponseDTO.class, payload, null, headers);

		return convertPullResponse(response, interfaceTemplateName);
	}

	//-------------------------------------------------------------------------------------------------
	private void lookupAndCacheOrchestration() {
		logger.debug("lookupAndCacheOrchestration started...");

		final String intfTemplateName = sysInfo.getSslProperties().isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;

		// try to lookup for dynamic orchestration service
		orchestrationCache = acquireServiceFromSR(Constants.SERVICE_DEF_SERVICE_ORCHESTRATION, intfTemplateName, Constants.SYS_NAME_DYNAMIC_SERVICE_ORCHESTRATION);

		// if unsuccessful, try to lookup for flexible store orchestration service
		if (orchestrationCache == null) {
			orchestrationCache = acquireServiceFromSR(Constants.SERVICE_DEF_SERVICE_ORCHESTRATION, intfTemplateName, Constants.SYS_NAME_FLEXIBLE_SERVICE_ORCHESTRATION);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceInstanceLookupRequestDTO createSRRequestPayload(final String serviceDefinitionName, final String interfaceTemplateName, final String providerName) {
		logger.debug("createSRRequestPayload started...");

		return new ServiceInstanceLookupRequestDTO.Builder()
				.providerName(providerName)
				.serviceDefinitionName(serviceDefinitionName)
				.interfaceTemplateName(interfaceTemplateName)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceInstanceLookupRequestDTO createSRRequestPayload(final String serviceDefinitionName, final String interfaceTemplateName) {
		logger.debug("createSRRequestPayload started...");

		return new ServiceInstanceLookupRequestDTO.Builder()
				.serviceDefinitionName(serviceDefinitionName)
				.interfaceTemplateName(interfaceTemplateName)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	private OrchestrationRequestDTO createOrchRequestPayload(final String serviceDefinitionName, final String interfaceTemplateName, final String providerName) {
		logger.debug("createOrchRequestPayload started...");

		final OrchestrationServiceRequirementDTO serviceRequirement = new OrchestrationServiceRequirementDTO.Builder()
				.serviceDefinition(serviceDefinitionName)
				.interfaceTemplateName(interfaceTemplateName)
				.preferredProvider(providerName)
				.build();

		final Map<String, Boolean> flags = Map.of(
				OrchestrationFlag.MATCHMAKING.toString(), true,
				OrchestrationFlag.ALLOW_INTERCLOUD.toString(), false,
				OrchestrationFlag.ALLOW_TRANSLATION.toString(), false,
				OrchestrationFlag.ONLY_PREFERRED.toString(), true);

		return new OrchestrationRequestDTO.Builder()
				.serviceRequirement(serviceRequirement)
				.orchestrationFlags(flags)
				.build();

	}

	//-------------------------------------------------------------------------------------------------
	private OrchestrationRequestDTO createOrchRequestPayload(final String serviceDefinitionName, final String interfaceTemplateName) {
		logger.debug("createOrchRequestPayload started...");

		final OrchestrationServiceRequirementDTO serviceRequirement = new OrchestrationServiceRequirementDTO.Builder()
				.serviceDefinition(serviceDefinitionName)
				.interfaceTemplateName(interfaceTemplateName).build();

		final Map<String, Boolean> flags = Map.of(
				OrchestrationFlag.MATCHMAKING.toString(), true,
				OrchestrationFlag.ALLOW_INTERCLOUD.toString(), false,
				OrchestrationFlag.ALLOW_TRANSLATION.toString(), false);

		return new OrchestrationRequestDTO.Builder()
				.serviceRequirement(serviceRequirement)
				.orchestrationFlags(flags)
				.build();

	}

	//-------------------------------------------------------------------------------------------------
	private ServiceModel convertLookupResponse(final ServiceInstanceListResponseDTO response, final String interfaceTemplateName) {
		logger.debug("convertLookupResponse started...");

		if (response.entries().isEmpty()) {
			return null;
		}

		// convert the first instance
		final ServiceInstanceResponseDTO instance = response.entries().getFirst();

		// create the list of interface models for the service model
		final List<InterfaceModel> interfaceModelList = convertInterfaceResponsesToInterfaceModels(instance.interfaces(), interfaceTemplateName);

		// build the service model
		return new ServiceModel.Builder()
				.serviceDefinition(instance.serviceDefinition().name())
				.version(instance.version())
				.metadata(instance.metadata())
				.serviceInterfaces(interfaceModelList)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceModel convertPullResponse(final OrchestrationResponseDTO response, final String interfaceTemplateName) {
		logger.debug("convertPullResponse started...");

		if (response.results().isEmpty()) {
			return null;
		}

		// convert the first instance
		final OrchestrationResultDTO instance = response.results().getFirst();

		// create the list of interface models for the service model
		final List<InterfaceModel> interfaceModelList = convertInterfaceResponsesToInterfaceModels(instance.interfaces(), interfaceTemplateName);

		// build the service model
		return new ServiceModel.Builder()
				.serviceDefinition(instance.serviceDefinitition())
				.version(instance.version())
				.metadata(instance.metadata())
				.serviceInterfaces(interfaceModelList)
				.build();
	}

	//-------------------------------------------------------------------------------------------------
	private List<InterfaceModel> convertInterfaceResponsesToInterfaceModels(final List<ServiceInstanceInterfaceResponseDTO> interfaces, final String interfaceTemplateName) {
		logger.debug("convertInterfaceResponsesToInterfaceModels started...");

		final List<InterfaceModel> interfaceModelList = new ArrayList<>();
		for (final ServiceInstanceInterfaceResponseDTO interf : interfaces) {

			final String templateName = interf.templateName();
			final Map<String, Object> properties = interf.properties();

			if (!interfaceTemplateName.equals(templateName)) {
				continue;
			}

			// HTTP or HTTPS
			if (templateName.contains(Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME)) {
				interfaceModelList.add(createHttpInterfaceModel(templateName, properties));
			}

			// MQTT or MQTTS
			if (templateName.contains(Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME)) {
				interfaceModelList.add(createMqttInterfaceModel(templateName, properties));
			}
		}

		return interfaceModelList;
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
		final HttpInterfaceModel model = new HttpInterfaceModel.Builder(templateName)
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
		final String topic = (String) properties.get(MqttInterfaceModel.PROP_NAME_BASE_TOPIC);

		// operations
		final Set<String> operations = properties.containsKey(MqttInterfaceModel.PROP_NAME_OPERATIONS)
				? new HashSet<String>((Collection<? extends String>) properties.get(MqttInterfaceModel.PROP_NAME_OPERATIONS))
				: Set.of();

		// create the interface model
		final MqttInterfaceModel model = new MqttInterfaceModel.Builder(templateName)
				.accessAddresses(accessAddresses)
				.accessPort(accessPort)
				.baseTopic(topic)
				.operations(operations)
				.build();

		return model;
	}
}