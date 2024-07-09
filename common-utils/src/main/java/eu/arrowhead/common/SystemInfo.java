package eu.arrowhead.common;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.model.ServiceModel;
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

	@Autowired
	private SSLProperties sslProperties;

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	private Map<String, Object> arrowheadContext;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public abstract String getSystemName();

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
	@PostConstruct
	private void init() {
		if (Utilities.isEmpty(getSystemName())) {
			throw new InvalidParameterException("'systemName' is missing or empty");
		}
		if (Utilities.isEmpty(domainAddress)) {
			throw new InvalidParameterException("'domainAddress' is missing or empty");
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
