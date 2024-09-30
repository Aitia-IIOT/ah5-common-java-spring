package eu.arrowhead.common.intf.properties.validators;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.intf.properties.IPropertyValidator;

@Service
public class HttpOperationsValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// propertyValue should be a map with string keys (operation name) and HttpOperationModel value
	// at least one pair should be in the map
	// an HttpOperationModel object should contains a not null method and a non-blank path
	// no need for normalization
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("HttpOperationsValidator.validateAndNormalize started...");

		if (propertyValue instanceof Map<?, ?>) {
			final Map<?, ?> map = (Map<?, ?>) propertyValue;
			if (map.isEmpty()) {
				throw new InvalidParameterException("Property value should be a non-empty map");
			}

			for (final Entry<?, ?> entry : map.entrySet()) {
				validateKey(entry.getKey());
				validateValue(entry.getValue());
			}

			return propertyValue;
		}

		throw new InvalidParameterException("Property value should be a map of operations");
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void validateKey(final Object key) {
		if (key instanceof String) {
			final String stringKey = (String) key;
			if (Utilities.isEmpty(stringKey)) {
				throw new InvalidParameterException("Key should be a non-empty string");
			}
		} else {
			throw new InvalidParameterException("Key should be a string");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void validateValue(final Object value) {
		if (value instanceof HttpOperationModel) {
			final HttpOperationModel model = (HttpOperationModel) value;
			if (Utilities.isEmpty(model.path())) {
				throw new InvalidParameterException("Path should be non-empty.");
			}

			if (model.method() == null) {
				throw new InvalidParameterException("methpd should be not null.");
			}
		} else {
			throw new InvalidParameterException("Value should be a HttpOperationModel record");
		}
	}
}