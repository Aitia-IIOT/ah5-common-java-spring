package eu.arrowhead.common.init;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Set;
import java.util.stream.Collectors;

import javax.security.auth.x500.X500Principal;

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
import eu.arrowhead.common.collector.ServiceCollector;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.common.mqtt.MqttController;
import eu.arrowhead.common.security.CertificateProfileType;
import eu.arrowhead.common.security.SecurityUtilities;
import eu.arrowhead.common.security.SecurityUtilities.CommonNameAndType;
import eu.arrowhead.dto.AddressDTO;
import eu.arrowhead.dto.ServiceInstanceCreateRequestDTO;
import eu.arrowhead.dto.ServiceInstanceInterfaceRequestDTO;
import eu.arrowhead.dto.ServiceInstanceResponseDTO;
import eu.arrowhead.dto.SystemRegisterRequestDTO;
import eu.arrowhead.dto.SystemResponseDTO;
import eu.arrowhead.dto.enums.ServiceInterfacePolicy;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

public abstract class ApplicationInitListener {

	//=================================================================================================
	// members

	protected final Logger logger = LogManager.getLogger(getClass());

	private static final int MAX_NUMBER_OF_SERVICEREGISTRY_CONNECTION_RETRIES = 3;
	private static final int WAITING_PERIOD_BETWEEN_RETRIES_IN_SECONDS = 15;

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	protected Map<String, Object> arrowheadContext;

	@Autowired
	protected SystemInfo sysInfo;

	@Autowired
	protected ServiceCollector serviceCollector;

	@Autowired
	protected ArrowheadHttpService arrowheadHttpService;

	@Autowired(required = false)
	protected MqttController mqttController;

	protected boolean standaloneMode = false;

	protected Set<String> registeredServices = new HashSet<>();

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

		if (arrowheadContext.containsKey(Constants.SERVER_STANDALONE_MODE)) {
			standaloneMode = (boolean) arrowheadContext.get(Constants.SERVER_STANDALONE_MODE);
		}

		if (sysInfo.isSslEnabled()) {
			final KeyStore keyStore = initializeKeyStore();
			obtainKeys(keyStore);
			if (sysInfo.getAuthenticationPolicy() == AuthenticationPolicy.CERTIFICATE) {
				// in this case the certificate must be compliant with the Arrowhead Certificate Structure
				checkServerCertificate(keyStore);
			}
		}

		registerToServiceRegistry();

		if (sysInfo.isMqttApiEnabled()) {
			subscribeToMqttServiceTopics();
		}

		customInit(event);

		logger.debug("Initialization in onApplicationEvent() is done.");
	}

	//-------------------------------------------------------------------------------------------------
	@PreDestroy
	public void destroy() throws InterruptedException {
		logger.debug("destroy called...");

		revokeServices();

		if (sysInfo.isMqttApiEnabled()) {
			mqttController.disconnect();
		}

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
		logger.debug("checkServerCertificate started...");
		final X509Certificate serverCertificate = (X509Certificate) arrowheadContext.get(Constants.SERVER_CERTIFICATE);
		final CommonNameAndType serverData = SecurityUtilities.getIdentificationDataFromSubjectDN(serverCertificate.getSubjectX500Principal().getName(X500Principal.RFC2253));

		if (serverData == null) {
			throw new AuthException("Server certificate is not compliant with the Arrowhead certificate structure, common name and profile type not found.");
		}
		if (CertificateProfileType.SYSTEM != serverData.profileType()) {
			throw new AuthException("Server certificate is not compliant with the Arrowhead certificate structure, invalid profile type: " + serverData.profileType());
		}

		if (!SecurityUtilities.isValidSystemCommonName(serverData.commonName())) {
			logger.error("Server CN ({}) is not compliant with the Arrowhead certificate structure, since it does not have 5 parts.", serverData.commonName());
			throw new AuthException("Server CN (" + serverData.commonName() + ") is not compliant with the Arrowhead certificate structure, since it does not have 5 parts.");
		}
		logger.info("Server CN: {}", serverData.commonName());

		arrowheadContext.put(Constants.SERVER_COMMON_NAME, serverData.commonName());
	}

	//-------------------------------------------------------------------------------------------------
	private void obtainKeys(final KeyStore keyStore) {
		logger.debug("obtainKeys started...");

		final X509Certificate serverCertificate = SecurityUtilities.getCertificateFromKeyStore(keyStore, sysInfo.getSslProperties().getKeyAlias());
		if (serverCertificate == null) {
			// never happens because checkServer
			throw new ServiceConfigurationError("Cannot find server certificate in the specified key store.");
		}

		arrowheadContext.put(Constants.SERVER_CERTIFICATE, serverCertificate);
		final PublicKey publicKey = serverCertificate.getPublicKey();
		arrowheadContext.put(Constants.SERVER_PUBLIC_KEY, publicKey);

		final PrivateKey privateKey = SecurityUtilities.getPrivateKeyFromKeyStore(keyStore, sysInfo.getSslProperties().getKeyAlias(), sysInfo.getSslProperties().getKeyPassword());
		if (privateKey == null) {
			throw new ServiceConfigurationError("Cannot find private key in the specified key store.");
		}
		arrowheadContext.put(Constants.SERVER_PRIVATE_KEY, privateKey);
	}

	//-------------------------------------------------------------------------------------------------
	private void subscribeToMqttServiceTopics() {
		logger.debug("subscribeToMqttServiceTopics started...");
		Assert.notNull(mqttController, "mqttController is null");

		if (Utilities.isEmpty(sysInfo.getServices())) {
			return;
		}

		for (final ServiceModel serviceModel : sysInfo.getServices()) {
			mqttController.listen(serviceModel);
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void registerToServiceRegistry() throws InterruptedException {
		logger.debug("registerToServiceRegistry started...");

		if (skipRegistration()) {
			return;
		}

		checkServiceRegistryConnection(sysInfo.isSslEnabled(), MAX_NUMBER_OF_SERVICEREGISTRY_CONNECTION_RETRIES, WAITING_PERIOD_BETWEEN_RETRIES_IN_SECONDS);

		// revoke system (if any)
		arrowheadHttpService.consumeService(Constants.SERVICE_DEF_SYSTEM_DISCOVERY, Constants.SERVICE_OP_REVOKE, Void.class);

		// register system
		final SystemModel model = sysInfo.getSystemModel();
		final List<AddressDTO> addresses = model.addresses()
				.stream()
				.map(a -> new AddressDTO(a.type().name(), a.address()))
				.collect(Collectors.toList());
		final SystemRegisterRequestDTO payload = new SystemRegisterRequestDTO(model.metadata(), model.version(), addresses, model.deviceName());
		arrowheadHttpService.consumeService(Constants.SERVICE_DEF_SYSTEM_DISCOVERY, Constants.SERVICE_OP_REGISTER, SystemResponseDTO.class, payload);

		// register services
		if (sysInfo.getServices() != null) {
			for (final ServiceModel serviceModel : sysInfo.getServices()) {
				registerService(serviceModel);
			}
		}

		logger.info("System {} published {} service(s).", sysInfo.getSystemName(), registeredServices.size());
	}

	//-------------------------------------------------------------------------------------------------
	private boolean skipRegistration() {
		logger.debug("skipRegistration started...");

		return standaloneMode || Constants.SYS_NAME_SERVICE_REGISTRY.equals(sysInfo.getSystemName());
	}

	//-------------------------------------------------------------------------------------------------
	private void checkServiceRegistryConnection(final boolean secure, final int retries, final int period) throws InterruptedException {
		logger.debug("checkServiceRegistryConnection started...");

		final String templateName = secure ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME;
		for (int i = 0; i <= retries; ++i) {
			try {
				serviceCollector.getServiceModel(Constants.SERVICE_DEF_SYSTEM_DISCOVERY, templateName);
				logger.info("Service Registry is accessable...");
				break;
			} catch (final ForbiddenException | AuthException ex) {
				throw ex;
			} catch (final ArrowheadException ex) {
				if (i >= retries) {
					throw ex;
				} else {
					logger.info("Service Registry is unavailable at the moment, retrying in {} seconds...", period);
					Thread.sleep(period * Constants.CONVERSION_MILLISECOND_TO_SECOND);
				}
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void registerService(final ServiceModel model) {
		logger.debug("registerService started...");

		final List<ServiceInstanceInterfaceRequestDTO> interfaces = model.interfaces()
				.stream()
				.map(i -> new ServiceInstanceInterfaceRequestDTO(i.templateName(), i.protocol(), ServiceInterfacePolicy.NONE.name(), i.properties()))
				.collect(Collectors.toList());
		final ServiceInstanceCreateRequestDTO payload = new ServiceInstanceCreateRequestDTO(model.serviceDefinition(), model.version(), null, model.metadata(), interfaces);
		final ServiceInstanceResponseDTO response = arrowheadHttpService.consumeService(Constants.SERVICE_DEF_SERVICE_DISCOVERY, Constants.SERVICE_OP_REGISTER, ServiceInstanceResponseDTO.class, payload);
		registeredServices.add(response.instanceId());
	}

	//-------------------------------------------------------------------------------------------------
	private void revokeServices() throws InterruptedException {
		logger.debug("revokeServices started...");

		if (skipRegistration()) {
			return;
		}

		try {
			checkServiceRegistryConnection(sysInfo.isSslEnabled(), 0, 1);

			for (final String serviceInstanceId : registeredServices) {
				arrowheadHttpService.consumeService(Constants.SERVICE_DEF_SERVICE_DISCOVERY, Constants.SERVICE_OP_REVOKE, Void.class, List.of(serviceInstanceId));
			}

			registeredServices.clear();
			logger.info("Core system {} revoked {} service(s).", sysInfo, registeredServices.size());
		} catch (final Throwable t) {
			logger.error(t.getMessage());
			logger.debug(t);
		}
	}
}