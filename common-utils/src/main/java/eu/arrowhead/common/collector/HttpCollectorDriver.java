package eu.arrowhead.common.collector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.http.HttpService;
import eu.arrowhead.common.model.ServiceModel;
import jakarta.annotation.Nullable;

public class HttpCollectorDriver implements ICollectorDriver {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value(Constants.$HTTP_COLLECTOR_MODE_WD)
	private HttpCollectorMode mode;

	@Autowired
	private HttpService httpService;

	@Autowired
	private SystemInfo sysInfo;

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
		Assert.isTrue(!Utilities.isEmpty(interfaceTemplateName), "template name is empty");

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
		// TODO Auto-generated method stub
		// create request, get authorization header
		// create uri manually
		// can't use ArrowheadHttpService because that cause circle referencing
		return null;
	}

	//-------------------------------------------------------------------------------------------------
	private ServiceModel acquireServiceFromOrchestrator(final String serviceDefinitionName, final String interfaceTemplateName) {
		// TODO Auto-generated method stub
		// try to orchestrate the service (if no orchestration service is cached, it will lookup for it first)
		return null;
	}
}