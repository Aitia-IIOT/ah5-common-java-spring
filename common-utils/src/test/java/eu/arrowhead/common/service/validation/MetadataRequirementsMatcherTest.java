package eu.arrowhead.common.service.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import eu.arrowhead.dto.MetadataRequirementDTO;

public class MetadataRequirementsMatcherTest {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isMetadataMatchNoRequirements() {
		assertAll("Empty requirements",
				() -> assertTrue(MetadataRequirementsMatcher.isMetadataMatch(null, null)),
				() -> assertTrue(MetadataRequirementsMatcher.isMetadataMatch(null, new MetadataRequirementDTO())));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isMetadataMatchNoMetadata() {
		final MetadataRequirementDTO req = new MetadataRequirementDTO();
		req.put("key", "value");

		assertAll("Empty metadata",
				() -> assertFalse(MetadataRequirementsMatcher.isMetadataMatch(null, req)),
				() -> assertFalse(MetadataRequirementsMatcher.isMetadataMatch(new HashMap<>(), req)));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void isMetadataMatchInvalidKey() {
		final Map<String, Object> metadata = Map.of("key", Map.of("subkey", "value"));
		final MetadataRequirementDTO req = new MetadataRequirementDTO();
		req.put("key.not_subkey", "value");

		assertFalse(MetadataRequirementsMatcher.isMetadataMatch(metadata, req));
	}
}
