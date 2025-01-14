package eu.arrowhead.common;

import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.model.SystemModel;
import eu.arrowhead.common.service.validation.address.AddressNormalizer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

public abstract class SystemInfo {

	//=================================================================================================
	// members

	@Value(Constants.$SERVER_ADDRESS)
	private String serverAddress;

	@Value(Constants.$SERVER_PORT)
	private int serverPort;

	@Value(Constants.$DOMAIN_NAME)
	private String domainAddress;

	@Value(Constants.$SERVICEREGISTRY_ADDRESS_WD)
	private String serviceRegistryAddress;

	@Value(Constants.$SERVICEREGISTRY_PORT_WD)
	private int serviceRegistryPort;

	@Value(Constants.$AUTHENTICATION_POLICY_WD)
	private AuthenticationPolicy authenticationPolicy;

	@Value(Constants.$MQTT_API_ENABLED_WD)
	private boolean mqttEnabled;

	@Value(Constants.$MQTT_BROKER_ADDRESS_WD)
	private String mqttBrokerAddress;

	@Value(Constants.$MQTT_BROKER_PORT_WD)
	private int mqttBrokerPort;

	@Value(Constants.$MQTT_CLIENT_PASSWORD)
	private String mqttClientPassword;

	@Autowired
	private SSLProperties sslProperties;

	@Autowired
	private AddressNormalizer addressNormalizer;

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	private Map<String, Object> arrowheadContext;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public abstract String getSystemName();

	//-------------------------------------------------------------------------------------------------
	public abstract SystemModel getSystemModel();

	//-------------------------------------------------------------------------------------------------
	public abstract List<ServiceModel> getServices();

	//-------------------------------------------------------------------------------------------------
	public String getIdentityToken() {
		return authenticationPolicy == AuthenticationPolicy.OUTSOURCED ? (String) arrowheadContext.get(Constants.KEY_IDENTITY_TOKEN) : null;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	protected void customInit() {
	};

	//-------------------------------------------------------------------------------------------------
	protected String getAddress() {
		return addressNormalizer.normalize(domainAddress);
	}

	//-------------------------------------------------------------------------------------------------
	protected String getPublicKey() {
		if (arrowheadContext.containsKey(Constants.SERVER_PUBLIC_KEY)) {
			final PublicKey publicKey = (PublicKey) arrowheadContext.get(Constants.SERVER_PUBLIC_KEY);

			return Base64.getEncoder().encodeToString(publicKey.getEncoded());
		}

		return "";
	}

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		if (Utilities.isEmpty(getSystemName())) {
			throw new InvalidParameterException("'systemName' is missing or empty");
		}

		if (Utilities.isEmpty(domainAddress)) {
			throw new InvalidParameterException("'domainAddress' is missing or empty");
		}

		if (mqttEnabled && Utilities.isEmpty(mqttBrokerAddress)) {
			throw new InvalidParameterException("MQTT Broker address is not defined");
		}

		customInit();
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public String getServerAddress() {
		return serverAddress;
	}

	//-------------------------------------------------------------------------------------------------
	public int getServerPort() {
		return serverPort;
	}

	//-------------------------------------------------------------------------------------------------
	public String getDomainAddress() {
		return domainAddress;
	}

	//-------------------------------------------------------------------------------------------------
	public String getServiceRegistryAddress() {
		return serviceRegistryAddress;
	}

	//-------------------------------------------------------------------------------------------------
	public int getServiceRegistryPort() {
		return serviceRegistryPort;
	}

	//-------------------------------------------------------------------------------------------------
	public AuthenticationPolicy getAuthenticationPolicy() {
		return authenticationPolicy;
	}

	//-------------------------------------------------------------------------------------------------
	public boolean isMqttApiEnabled() {
		return this.mqttEnabled;
	}

	//-------------------------------------------------------------------------------------------------
	public String getMqttBrokerAddress() {
		return this.mqttBrokerAddress;
	}

	//-------------------------------------------------------------------------------------------------
	public String getMqttClientPassword() {
		return this.mqttClientPassword;
	}

	//-------------------------------------------------------------------------------------------------
	public int getMqttBrokerPort() {
		return this.mqttBrokerPort;
	}

	//-------------------------------------------------------------------------------------------------
	public SSLProperties getSslProperties() {
		return sslProperties;
	}

	//-------------------------------------------------------------------------------------------------
	public Map<String, Object> getArrowheadContext() {
		return arrowheadContext;
	}

	//-------------------------------------------------------------------------------------------------
	public boolean isSslEnabled() {
		return sslProperties != null && sslProperties.isSslEnabled();
	}
}