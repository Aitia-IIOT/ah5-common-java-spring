package eu.arrowhead.common.service.validation;

import java.util.List;
import java.util.Map;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.service.validation.meta.MetadataRequirementExpression;
import eu.arrowhead.common.service.validation.meta.MetadataRequirementTokenizer;
import eu.arrowhead.dto.MetadataRequirementDTO;

public final class MetadataRequirementsMatcher {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static boolean isMetadataMatch(final Map<String, Object> metadata, final MetadataRequirementDTO requirements) throws InvalidParameterException {
		if (Utilities.isEmpty(requirements)) {
			return true;
		}

		if (Utilities.isEmpty(metadata)) {
			return false;
		}

		final List<MetadataRequirementExpression> expressions = MetadataRequirementTokenizer.parseRequirements(requirements);
		for (final MetadataRequirementExpression expression : expressions) {
			// TODO: MetadataKeyEvaluator finds the value for specified key
			// TODO: use op evaluator with parameters: value from metadata as left and value from requirements as right => is eval is false return false

		}

		return true;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private MetadataRequirementsMatcher() {
		throw new UnsupportedOperationException();
	}
}
