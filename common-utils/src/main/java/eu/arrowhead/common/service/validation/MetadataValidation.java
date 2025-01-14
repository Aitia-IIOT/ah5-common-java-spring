package eu.arrowhead.common.service.validation;

import java.util.List;
import java.util.Map;

import eu.arrowhead.common.exception.InvalidParameterException;

public final class MetadataValidation {

	//=================================================================================================
	// members

	public static final String DOT = ".";

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void validateMetadataKey(final Map<String, Object> metadata) throws InvalidParameterException {
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
		} else if (metadataObject instanceof final List list) {
			for (final Object element : list) {
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

		final Map<?, ?> map = (Map<?, ?>) object;
		boolean isValid = true;

		for (final Map.Entry<?, ?> entry : map.entrySet()) {
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