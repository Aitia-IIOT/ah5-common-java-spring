package eu.arrowhead.common.intf.properties.validators;

import org.springframework.stereotype.Service;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.intf.properties.IPropertyValidator;

@Service
public class MinMaxValidator implements IPropertyValidator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public Object validateNormalize(final Object property, final String... args) throws InvalidParameterException {
		// TODO Auto-generated method stub
		return property;
	}

}
