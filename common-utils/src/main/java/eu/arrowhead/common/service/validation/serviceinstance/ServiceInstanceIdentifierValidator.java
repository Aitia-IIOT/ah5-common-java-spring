package eu.arrowhead.common.service.validation.serviceinstance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.service.validation.name.ServiceDefinitionNameValidator;
import eu.arrowhead.common.service.validation.name.SystemNameValidator;
import eu.arrowhead.common.service.validation.version.VersionValidator;

@Component
public class ServiceInstanceIdentifierValidator {

	//=================================================================================================
	// members

	@Autowired
	private SystemNameValidator systemNameValidator;

	@Autowired
	private ServiceDefinitionNameValidator serviceDefNameValidator;

	@Autowired
	private VersionValidator versionValidator;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:magicnumber")
	public void validateServiceInstanceIdentifier(final String identifier) {
		logger.debug("validateServiceInstanceIdentifier started: {}", identifier);

		if (Utilities.isEmpty(identifier)) {
			throw new InvalidParameterException("The specified service instance identifier does not match the naming convention: " + identifier);

		}

		// accepted format <ProviderName><delimiter><serviceDefinitionName><delimiter><semantic version>
		final String[] parts = identifier.split(Constants.COMPOSITE_ID_DELIMITER_REGEXP);
		if (parts.length != 3) {
			throw new InvalidParameterException("The specified service instance identifier does not match the naming convention: " + identifier);
		}

		systemNameValidator.validateSystemName(parts[0]);
		serviceDefNameValidator.validateServiceDefinitionName(parts[1]);
		versionValidator.validateNormalizedVersion(parts[2]);
	}
}