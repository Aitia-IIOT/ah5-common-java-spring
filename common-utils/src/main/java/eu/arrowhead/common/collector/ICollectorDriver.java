package eu.arrowhead.common.collector;

import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.model.ServiceModel;

public interface ICollectorDriver {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void init() throws ArrowheadException;

	//-------------------------------------------------------------------------------------------------
	public ServiceModel acquireService(final String serviceDefinitionName, final String interfaceTemplateName, final String providerName) throws ArrowheadException;
}