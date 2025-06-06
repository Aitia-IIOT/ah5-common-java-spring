package eu.arrowhead.common.service.validation.serviceinstance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.service.validation.name.ServiceDefinitionNameNormalizer;
import eu.arrowhead.common.service.validation.name.SystemNameNormalizer;
import eu.arrowhead.common.service.validation.version.VersionNormalizer;

@Component
public class ServiceInstanceIdentifierNormalizer {

	//=================================================================================================
	// members

	@Autowired
	private SystemNameNormalizer systemNameNormalizer;

	@Autowired
	private ServiceDefinitionNameNormalizer serviceDefNameNormalizer;

	@Autowired
	private VersionNormalizer versionNormalizer;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String normalize(final String serviceInstanceId) {
		logger.debug("normalize service instance identitifer started...");

		if (serviceInstanceId == null) {
			return null;
		}

		final String[] parts = serviceInstanceId.split(Constants.COMPOSITE_ID_DELIMITER_REGEXP);
		parts[0] = systemNameNormalizer.normalize(parts[0]);

		if (parts.length > 1) {
			parts[1] = serviceDefNameNormalizer.normalize(parts[1]);
		}

		if (parts.length > 2) {
			parts[2] = versionNormalizer.normalize(parts[2]);
		}

		return String.join(Constants.COMPOSITE_ID_DELIMITER, parts);
	}
}