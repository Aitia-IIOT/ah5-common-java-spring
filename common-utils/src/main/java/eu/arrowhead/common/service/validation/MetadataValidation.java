package eu.arrowhead.common.service.validation;

import java.util.List;
import java.util.Map;

import eu.arrowhead.common.exception.InvalidParameterException;

public final class MetadataValidation {
	//=================================================================================================
	// members

	private static final String DOT = ".";

	//=================================================================================================
	// methods
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public static void validateMetadataKey(final Object metadata) {

		if (isValidMap(metadata)) {
			final Map<String, Object> map = (Map<String, Object>) metadata;
			for (final String key : map.keySet()) {

				// check the key
				if (key.contains(DOT)) {
					throw new InvalidParameterException("Invalid metadata key: " + key + ", it should not contain " + DOT + " character!");
				}

				// check the value
				validateMetadataKey(map.get(key));
			}
		}

		if (metadata instanceof List) {
			for (Object element : (List<?>) metadata) {
				validateMetadataKey(element);
			}
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	// checks if the type of map is Map<String, Object>
	private static boolean isValidMap(final Object object) {
		if (!(object instanceof Map)) {
			return false;
		}
		Map<?, ?> map = (Map<?, ?>) object;
		boolean isValid = true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof Object)) {
                isValid = false;
                break;
            }
        }
        return isValid;
	}

	//-------------------------------------------------------------------------------------------------
	private MetadataValidation() {
		throw new UnsupportedOperationException();
	}
}
