package eu.arrowhead.common.service.validation.name;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.service.normalization.NormalizationMode;

@Component
public class EventTypeNameNormalizer {

	//=================================================================================================
	// members

	@Value(Constants.$NORMALIZATION_MODE_WD)
	private NormalizationMode normalizationMode;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String normalize(final String name) {
		logger.debug("EventTypeNameNormalizer.normalize started...");

		if (Utilities.isEmpty(name)) {
			return null;
		}

		String result = name.trim();

		if (NormalizationMode.EXTENDED == normalizationMode) {
			result = transformName(result);
		}

		return result;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private String transformName(final String name) {
		logger.debug("EventTypeNameNormalizer.transformName started...");

		// replace all delimiter chunks (hyphens and underscores) with a single underscore
		String result = name.replaceAll(NormalizationUtils.DELIMITER_REGEXP, NormalizationUtils.UNDERSCORE);
		// replaces chunks of one or more consecutive whitespaces with a single underscore
		result = result.replaceAll(NormalizationUtils.WHITESPACE_REGEXP, NormalizationUtils.UNDERSCORE);

		return NormalizationUtils.convertSnakeCaseToCamelCase(result);
	}
}