package eu.arrowhead.common.intf.properties.validators;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.intf.properties.IPropertyValidator;
import eu.arrowhead.common.service.validation.name.NameNormalizer;
import eu.arrowhead.common.service.validation.name.NameValidator;

@Service
public class NotEmptySetValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	@Autowired
	private NameValidator nameValidator;

	@Autowired
	private NameNormalizer nameNormalizer;

	private final String nameArg = "name";

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("NotEmptySetValidator.validateAndNormalize started...");

		if (propertyValue instanceof final Set<?> set) {
			if (set.isEmpty()) {
				throw new InvalidParameterException("Property value should be a non-empty set");
			}

			final Set<String> normalized = new HashSet<>(set.size());
			final boolean isName = (args.length > 0 ? args[0] : "").trim().equalsIgnoreCase(nameArg);
			for (final Object object : set) {
				if (object instanceof final String str) {
					if (isName) {
						nameValidator.validateName(str.trim());
						normalized.add(nameNormalizer.normalize(str));
					} else {
						final String trimmed = str.trim();
						if (Utilities.isEmpty(trimmed)) {
							throw new InvalidParameterException("Value should be a non-empty string");
						}
					}
				} else {
					throw new InvalidParameterException("Value should be a string");
				}
			}
		}

		throw new InvalidParameterException("Property value should be a set of string values");
	}

}
