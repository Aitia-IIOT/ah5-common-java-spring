package eu.arrowhead.common.service.normalization;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ConfigNormalizationTest {

	//=================================================================================================
	// members

	private final ConfigNormalization normalizer = new ConfigNormalization();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeConfigKeyListEmptyInput() {
		assertAll("empty input",
				() -> assertEquals(List.of(), normalizer.normalizeConfigKeyList(null)),
				() -> assertEquals(List.of(), normalizer.normalizeConfigKeyList(List.of())));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeConfigKeyListOk() {
		final List<String> expected = List.of("key1", "key2");

		assertEquals(expected, normalizer.normalizeConfigKeyList(List.of(" key1", "key1", "key2 ")));
	}
}