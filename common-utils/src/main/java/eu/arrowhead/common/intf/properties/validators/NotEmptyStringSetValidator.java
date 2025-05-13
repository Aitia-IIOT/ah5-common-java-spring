package eu.arrowhead.common.intf.properties.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.intf.properties.IPropertyValidator;
import eu.arrowhead.common.service.validation.name.ServiceOperationNameNormalizer;
import eu.arrowhead.common.service.validation.name.ServiceOperationNameValidator;

@Service
public class NotEmptyStringSetValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	private static final String ARG_OPERATION = "OPERATION";

	@Autowired
	private ServiceOperationNameNormalizer operationNameNormalizer;

	@Autowired
	private ServiceOperationNameValidator operationNameValidator;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("NotEmptyStringSetValidator.validateAndNormalize started...");

		if (propertyValue instanceof final Collection<?> set) {
			if (set.isEmpty()) {
				throw new InvalidParameterException("Property value should be a non-empty set");
			}

			final Set<String> normalized = new HashSet<>(set.size());
			final boolean isOperation = (args.length > 0 ? args[0] : "").trim().equalsIgnoreCase(ARG_OPERATION);
			for (final Object object : set) {
				if (object instanceof final String str) {
					if (isOperation) {
						final String normalizedStr = operationNameNormalizer.normalize(str);
						operationNameValidator.validateServiceOperationName(normalizedStr);
						normalized.add(normalizedStr);
					} else {
						if (Utilities.isEmpty(str)) {
							throw new InvalidParameterException("Value should be a non-empty string");
						}
						normalized.add(str.trim());
					}
				} else {
					throw new InvalidParameterException("Value should be a string");
				}
			}

			return normalized;
		}

		throw new InvalidParameterException("Property value should be a set of string values");
	}
}