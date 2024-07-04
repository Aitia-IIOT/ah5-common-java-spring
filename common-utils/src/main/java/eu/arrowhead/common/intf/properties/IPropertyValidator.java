package eu.arrowhead.common.intf.properties;

import eu.arrowhead.common.exception.InvalidParameterException;

public interface IPropertyValidator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void validate(final Object property, final String... args) throws InvalidParameterException;
}
