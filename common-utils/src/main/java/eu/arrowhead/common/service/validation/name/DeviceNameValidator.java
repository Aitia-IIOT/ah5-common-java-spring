package eu.arrowhead.common.service.validation.name;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DeviceNameValidator {

	//=================================================================================================
	// members

	// snake-case naming convention, only allowed characters are upper-case ASCII letters and numbers and underscore
	private static final String DEVICE_NAME_REGEX_STRING = "([A-Z]{1})|(^[A-Z][0-9A-Z_]*[0-9A-Z]$)";
	private static final Pattern DEVICE_NAME_REGEX_PATTERN = Pattern.compile(DEVICE_NAME_REGEX_STRING);

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void validateDeviceName(final String name) {
		logger.debug("validateDeviceName started: {}", name);

		if (Utilities.isEmpty(name)
				|| !DEVICE_NAME_REGEX_PATTERN.matcher(name).matches()
				|| name.length() > Constants.DEVICE_NAME_MAX_LENGTH) {
			throw new InvalidParameterException("The specified device name does not match the naming convention: " + name);
		}
	}
}