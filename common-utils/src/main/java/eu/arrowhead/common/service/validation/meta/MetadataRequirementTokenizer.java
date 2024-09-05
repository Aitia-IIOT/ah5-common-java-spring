package eu.arrowhead.common.service.validation.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.dto.MetadataRequirementDTO;

public final class MetadataRequirementTokenizer {

	//=================================================================================================
	// members

	private static final String OP = "op";
	private static final String VALUE = "value";

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static List<MetadataRequirementExpression> parseRequirements(final MetadataRequirementDTO requirements) throws InvalidParameterException {
		final List<MetadataRequirementExpression> result = new ArrayList<>(requirements.size());

		for (final Entry<String, Object> req : requirements.entrySet()) {
			result.add(parseRequirement(req.getKey(), req.getValue()));
		}

		return result;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private MetadataRequirementTokenizer() {
		throw new UnsupportedOperationException();
	}

	//-------------------------------------------------------------------------------------------------
	private static MetadataRequirementExpression parseRequirement(final String key, final Object value) throws InvalidParameterException {
		if (value instanceof Map<?, ?>) {
			final Map<?, ?> opMap = (Map<?, ?>) value;
			if (opMap.containsKey(OP) && opMap.containsKey(VALUE)) {
				final String opStr = opMap.get(OP).toString();
				final Object valueReq = opMap.get(VALUE);

				try {
					final MetaOps opReq = MetaOps.valueOf(opStr.trim().toUpperCase());

					return new MetadataRequirementExpression(key, opReq, valueReq);
				} catch (final IllegalArgumentException ex) {
					throw new InvalidParameterException("Invalid metadata operation requirement: " + opStr);
				}
			}

			// otherwise EQUALS operation is used on a Map value
		}

		return new MetadataRequirementExpression(key, MetaOps.EQUALS, value);
	}
}