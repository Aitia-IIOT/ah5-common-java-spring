package eu.arrowhead.common.service.validation.name;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.service.validation.version.VersionValidator;

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class NameValidator {

	//=================================================================================================
	// members

	private static final Logger logger = LogManager.getLogger(NameValidator.class);

	private static final String NAME_REGEX_STRING = "([A-Za-z]{1})|(^[A-Za-z][0-9A-Za-z\\-]*[0-9A-Za-z]$)";
	private static final Pattern NAME_REGEX_PATTERN = Pattern.compile(NAME_REGEX_STRING);
	
	// convention for service instance id: <provider-name>::<service-definition>::<version>
	private static final String DOUBLE_COLON = "::";
	private static int serviceInstanceIdParts = 3;
	private static int providerNameIdx = 0;
	private static int serviceDefinitionNameIdx = 1;
	private static int versionIdx = 2;

	@Autowired
	private VersionValidator versionValidator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void validateName(final String name) {
		logger.debug("Validate name started: {}", name);
		Assert.notNull(name, "name is null!");

		if (!NAME_REGEX_PATTERN.matcher(name).matches()) {
			throw new InvalidParameterException("The specified name does not match the naming convention: " + name);
		}
	}

	//-------------------------------------------------------------------------------------------------
	public void validateServiceInstanceId(final String serviceInstanceId) {
		// naming convention for service instance id: <provider-name>::<service-definition>::<version>
		logger.debug("Validate service instance id started: {}", serviceInstanceId);
		Assert.notNull(serviceInstanceId, "serviceInstanceId is null!");
		
		String[] parts = serviceInstanceId.split(DOUBLE_COLON);

		if (parts.length != serviceInstanceIdParts) {
			throw new InvalidParameterException("Service instance id should consist of three parts separated by '" + DOUBLE_COLON + "'");
		}

		validateName(parts[providerNameIdx]);
		validateName(parts[serviceDefinitionNameIdx]);
		versionValidator.validateNormalizedVersion(parts[versionIdx]);
	}
}