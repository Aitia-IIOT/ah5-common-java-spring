package eu.arrowhead.common.model;

import java.util.Map;

public interface InterfaceModel {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String templateName();
	public String protocol();
	public Map<String, Object> properties();
}
