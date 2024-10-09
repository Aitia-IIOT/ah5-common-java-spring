package eu.arrowhead.common.intf.properties.validators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.intf.properties.IPropertyValidator;

@Service
public class MinMaxValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// propertyValue should be a number
	// args has at least 2 elements, both are the string representation of a number
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("MinMaxValidator.validateAndNormalize started...");

		if (propertyValue instanceof Number) {
			if (args == null || args.length < 2) {
				throw new InvalidParameterException("Missing minimum and/or maximum values.");
			}

			final boolean isReal = (propertyValue instanceof Double || propertyValue instanceof Float);

			if (isReal) {
				try {
					final double min = Double.parseDouble(args[0]);
					final double max = Double.parseDouble(args[1]);
					final double value = ((Number) propertyValue).doubleValue();

					if (value < min || value > max) {
						throw new InvalidParameterException("Invalid property value.");
					}
				} catch (final NumberFormatException ex) {
					throw new InvalidParameterException("Invalid minimum and/or maximum values.");
				}
			} else {
				try {
					final long min = Long.parseLong(args[0]);
					final long max = Long.parseLong(args[1]);
					final long value = ((Number) propertyValue).longValue();

					if (value < min || value > max) {
						throw new InvalidParameterException("Invalid property value.");
					}
				} catch (final NumberFormatException ex) {
					throw new InvalidParameterException("Invalid minimum and/or maximum values.");
				}
			}

			return propertyValue;
		}

		throw new InvalidParameterException("Property value should be a number");
	}
}
