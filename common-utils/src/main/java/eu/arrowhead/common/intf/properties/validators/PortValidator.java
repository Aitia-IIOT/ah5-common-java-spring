package eu.arrowhead.common.intf.properties.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.intf.properties.IPropertyValidator;

@Service
public class PortValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	@SuppressWarnings("checkstyle:nowhitespaceafter")
	private static String[] minMax = { String.valueOf(Constants.MIN_PORT), String.valueOf(Constants.MAX_PORT) };

	private final Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private MinMaxValidator minMaxValidator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// propertyValue should be an integer number
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("PortValidator.validateAndNormalize started...");

		if (propertyValue instanceof Number && !(propertyValue instanceof Double || propertyValue instanceof Float)) {
			return minMaxValidator.validateAndNormalize(propertyValue, minMax);
		}

		throw new InvalidParameterException("Property value should be an integer");
	}
}
