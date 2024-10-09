package eu.arrowhead.common.intf.properties;

import eu.arrowhead.common.exception.InvalidParameterException;

public interface IPropertyValidator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException;
}
