package eu.arrowhead.common.service.validation.name;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class EventTypeNameValidator {

	//=================================================================================================
	// members

	// camelCase naming convention, only allowed characters are lower- and upper-case ASCII letters and numbers
	private static final String EVENT_TYPE_NAME_REGEX_STRING = "([a-z]{1})|(^[a-z][0-9A-Za-z]+$)";
	private static final Pattern EVENT_TYPE_NAME_REGEX_PATTERN = Pattern.compile(EVENT_TYPE_NAME_REGEX_STRING);

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void validateEventTypeName(final String name) {
		logger.debug("validateEventTypeName started: {}", name);

		if (Utilities.isEmpty(name)
				|| !EVENT_TYPE_NAME_REGEX_PATTERN.matcher(name).matches()
				|| name.length() > Constants.EVENT_TYPE_NAME_MAX_LENGTH) {
			throw new InvalidParameterException("The specified event type name does not match the naming convention: " + name);
		}
	}
}