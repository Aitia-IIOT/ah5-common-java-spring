package eu.arrowhead.common.collector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.model.ServiceModel;

public class HttpCollectorDriver implements ICollectorDriver {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void init() throws ArrowheadException {
		logger.debug("HttpCollectorDriver.init started...");
		// TODO Auto-generated method stub

		// try to lookup orchestration service and cache it
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public ServiceModel acquireService(final String serviceDefinitionName, final String interfaceTemplateName) throws ArrowheadException {
		// TODO Auto-generated method stub

		// first try to lookup the service directly from the SR
		// if not succeeded, try to orchestrate the service (if no orchestration service is cached, it will lookup for it first)

		return null;
	}

}
