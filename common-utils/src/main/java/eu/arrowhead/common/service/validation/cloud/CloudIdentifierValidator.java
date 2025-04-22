package eu.arrowhead.common.service.validation.cloud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.service.validation.name.NameValidator;

@Component
public class CloudIdentifierValidator {

	//=================================================================================================
	// members

	public static final String CLOUD_IDENTIFIER_DELIMITER_REGEX = "\\.";

	@Autowired
	private NameValidator nameValidator;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void validateCloudIdentifier(final String identifier) {
		logger.debug("validateCloudIdentifier started: {}", identifier);

		if (Utilities.isEmpty(identifier)) {
			throw new InvalidParameterException("The specified cloud identifier does not match the naming convention: " + identifier);

		}

		if (!Defaults.DEFAULT_CLOUD.equals(identifier.trim())) {
			// accepted format <cloud-name>.<organization-name>
			final String[] parts = identifier.trim().split(CLOUD_IDENTIFIER_DELIMITER_REGEX);
			if (parts.length != 2) {
				throw new InvalidParameterException("The specified cloud identifier does not match the naming convention: " + identifier);
			}

			for (final String part : parts) {
				nameValidator.validateName(part);
			}
		}
	}
}