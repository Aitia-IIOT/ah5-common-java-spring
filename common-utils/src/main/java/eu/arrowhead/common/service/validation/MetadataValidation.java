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
	public static void validateMetadataKey(final Map<String, Object> metadata) {
		validateMetadataObjectKey(metadata);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private static void validateMetadataObjectKey(final Object metadataObject) {

		if (isValidMap(metadataObject)) {
			final Map<String, Object> map = (Map<String, Object>) metadataObject;
			for (final String key : map.keySet()) {

				// check the key
				if (key.contains(DOT)) {
					throw new InvalidParameterException("Invalid metadata key: " + key + ", it should not contain " + DOT + " character!");
				}

				// check the value
				validateMetadataObjectKey(map.get(key));
			}
		}

		if (metadataObject instanceof List) {
			for (Object element : (List<?>) metadataObject) {
				validateMetadataObjectKey(element);
			}
		}
	}

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
