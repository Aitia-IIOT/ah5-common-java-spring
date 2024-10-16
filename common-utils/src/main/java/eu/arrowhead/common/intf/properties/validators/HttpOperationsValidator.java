package eu.arrowhead.common.intf.properties.validators;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.HttpUtilities;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.intf.properties.IPropertyValidator;
import eu.arrowhead.common.service.validation.name.NameNormalizer;
import eu.arrowhead.common.service.validation.name.NameValidator;

@Service
public class HttpOperationsValidator implements IPropertyValidator {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private NameValidator nameValidator;

	@Autowired
	private NameNormalizer nameNormalizer;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	// propertyValue should be a map with string keys (operation name that should follow the naming conventions) and HttpOperationModel value
	// at least one pair should be in the map
	// an HttpOperationModel object should contain a not null method and a non-blank path
	// normalization: operation names will be trimmed and changed to lower-case
	// normalization: path will be trimmed
	@Override
	public Object validateAndNormalize(final Object propertyValue, final String... args) throws InvalidParameterException {
		logger.debug("HttpOperationsValidator.validateAndNormalize started...");

		if (propertyValue instanceof Map<?, ?> map) {
			if (map.isEmpty()) {
				throw new InvalidParameterException("Property value should be a non-empty map");
			}

			final Map<String, HttpOperationModel> normalized = new HashMap<>(map.size());
			for (final Entry<?, ?> entry : map.entrySet()) {
				final String normalizedKey = validateKey(entry.getKey());
				final HttpOperationModel normalizedModel = validateValue(entry.getValue());

				normalized.put(normalizedKey, normalizedModel);
			}

			return normalized;
		}

		throw new InvalidParameterException("Property value should be a map of operations");
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private String validateKey(final Object key) {
		if (key instanceof String stringKey) {
			nameValidator.validateName(stringKey.trim());

			return nameNormalizer.normalize(stringKey);
		} else {
			throw new InvalidParameterException("Key should be a string");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private HttpOperationModel validateValue(final Object value) {
		try {
			// value should be a map which has the exact same structure that a HttpOperationModel => try to convert it
			final HttpOperationModel model = Utilities.fromJson(Utilities.toJson(value), HttpOperationModel.class);
			if (Utilities.isEmpty(model.path())) {
				throw new InvalidParameterException("Path should be non-empty.");
			}

			if (!HttpUtilities.isValidHttpMethod(model.method())) {
				throw new InvalidParameterException("Method should be a standard HTTP method.");
			}

			return new HttpOperationModel(model.path().trim(), model.method().toUpperCase().trim());

		} catch (final ArrowheadException ex) {
			throw new InvalidParameterException("Value should be a HttpOperationModel record");
		}
	}
}