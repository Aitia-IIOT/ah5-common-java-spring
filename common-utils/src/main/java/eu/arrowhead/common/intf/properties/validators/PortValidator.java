package eu.arrowhead.common.intf.properties.validators;

import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.intf.properties.IPropertyValidator;

@Service
public class PortValidator extends MinMaxValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	@SuppressWarnings("checkstyle:nowhitespaceafter")
	private static String[] minMax = { String.valueOf(Constants.MIN_PORT), String.valueOf(Constants.MAX_PORT) };

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// propertyValue should be an integer number
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("PortValidator.validateAndNormalize started...");

		if (propertyValue instanceof Number) {
			final boolean isReal = (propertyValue instanceof Double || propertyValue instanceof Float);
			if (isReal) {
				throw new InvalidParameterException("Property value should be an integer");
			}

			return super.validateAndNormalize(propertyValue, minMax);
		}

		throw new InvalidParameterException("Property value should be an integer");
	}
}
