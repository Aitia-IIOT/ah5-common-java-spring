package eu.arrowhead.common.service.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import eu.arrowhead.common.exception.InvalidParameterException;

public class MetadataValidationTest {

	//=================================================================================================
	// members

	private static final String KEY = "key";
	private static final String INVALID_KEY = "key.key";
	private static final String VALUE = "value";
	private static final String EXPECTED_ERROR_MESSAGE = "Invalid metadata key: " + INVALID_KEY + ", it should not contain . character!";

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isMetadataValid() {
		final Map<String, Object> simpleMetadata = Map.of(KEY, VALUE);
		final Map<String, Object> mapMetadata = Map.of(KEY, Map.of(KEY, VALUE));
		final Map<String, Object> listMetadata = Map.of(KEY, List.of(VALUE, VALUE));
		final Map<String, Object> mapInListMetadata = Map.of(KEY, List.of(Map.of(KEY, VALUE), Map.of(KEY, VALUE)));

		assertAll(
			() -> MetadataValidation.validateMetadataKey(simpleMetadata),
			() -> MetadataValidation.validateMetadataKey(mapMetadata),
			() -> MetadataValidation.validateMetadataKey(listMetadata),
			() -> MetadataValidation.validateMetadataKey(mapInListMetadata));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isKeyInvalid() {
		final Map<String, Object> invalidMetadata = Map.of(INVALID_KEY, VALUE);

        final Throwable ex = assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidMetadata);
        });

        assertEquals(EXPECTED_ERROR_MESSAGE, ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isSubkeyInMapInvalid() {
		final Map<String, Object> invalidMapMetadata = Map.of(KEY, Map.of(INVALID_KEY, VALUE));
		final Map<String, Object> invalidMapMetadata_LongerMap = Map.of(KEY, Map.of(KEY, VALUE, INVALID_KEY, VALUE));
		final Map<String, Object> invalidMapMetadata_MapInMap = Map.of(KEY, Map.of(KEY, Map.of(INVALID_KEY, VALUE)));

        assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidMapMetadata);
        });

        assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidMapMetadata_LongerMap);
        });

        assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidMapMetadata_MapInMap);
        });
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isSubkeyInListInvalid() {
		final Map<String, Object> invalidListMetadata = Map.of(KEY, List.of(Map.of(INVALID_KEY, VALUE)));
		final Map<String, Object> invalidListMetadata_ListInList = Map.of(KEY, List.of(List.of(Map.of(KEY, VALUE), Map.of(INVALID_KEY, VALUE))));

		assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidListMetadata);
        });

        assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidListMetadata_ListInList);
        });
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isVeryComplicatedMetadataInvalid() {
		final Map<String, Object> invalidVeryComplicatedMetadata =
				Map.of(KEY, List.of(List.of(KEY, Map.of(KEY, VALUE), Map.of(KEY, Map.of(KEY, List.of(Map.of(KEY, VALUE), Map.of(KEY, VALUE, INVALID_KEY, VALUE)))))));

		assertThrows(InvalidParameterException.class, () -> {
        	MetadataValidation.validateMetadataKey(invalidVeryComplicatedMetadata);
        });
	}
}