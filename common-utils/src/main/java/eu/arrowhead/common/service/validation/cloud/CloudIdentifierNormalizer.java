package eu.arrowhead.common.service.validation.cloud;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.service.validation.name.NameNormalizer;

@Component
public class CloudIdentifierNormalizer {

	//=================================================================================================
	// members

	@Autowired
	private NameNormalizer nameNormalizer;

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

		return nameNormalizer.normalize(cloudIdentifier);
	}
}