package eu.arrowhead.common.service.validation.cloud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.service.validation.name.SystemNameNormalizer;

@Component
public class CloudIdentifierNormalizer {

	//=================================================================================================
	// members

	@Autowired
	private SystemNameNormalizer systemNameNormalizer; // the two parts of the cloud identifier should follow the naming convention of systems

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String normalize(final String cloudIdentifier) {
		logger.debug("normalize cloud identitifer started...");

		if (cloudIdentifier == null) {
			return null;
		}

		if (Defaults.DEFAULT_CLOUD.equalsIgnoreCase(cloudIdentifier.trim())) {
			return Defaults.DEFAULT_CLOUD;
		}

		final String[] parts = cloudIdentifier.split(Constants.COMPOSITE_ID_DELIMITER_REGEXP);
		for (int i = 0; i < parts.length; ++i) {
			parts[i] = systemNameNormalizer.normalize(parts[i]);
		}

		return String.join(Constants.COMPOSITE_ID_DELIMITER, parts);
	}
}