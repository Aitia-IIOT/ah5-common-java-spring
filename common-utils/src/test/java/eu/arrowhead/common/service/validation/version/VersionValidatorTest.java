package eu.arrowhead.common.service.validation.version;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eu.arrowhead.common.exception.InvalidParameterException;

public class VersionValidatorTest {

	//=================================================================================================
	// members

	private final VersionValidator validator = new VersionValidator();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateNormalizedVersionSizeProblem() {
		final Throwable ex = assertThrows(
				InvalidParameterException.class,
				() -> validator.validateNormalizedVersion("12345.67890.12345"));
		assertEquals("Version verification failure: version is too long", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateNormalizedVersionFormatProblem() {
		final Throwable ex = assertThrows(
				InvalidParameterException.class,
				() -> validator.validateNormalizedVersion("1.2.3.4"));
		assertEquals("Version verification failure: version must contain exactly 2 separator . characters", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateNormalizedVersionFormatProblem2() {
		final Throwable ex = assertThrows(
				InvalidParameterException.class,
				() -> validator.validateNormalizedVersion("1.23."));
		assertEquals("Version verification failure: version must contain exactly 3 numbers", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateNormalizedVersionTypeProblem() {
		final Throwable ex = assertThrows(
				InvalidParameterException.class,
				() -> validator.validateNormalizedVersion("1.23.SOMETHING"));
		assertEquals("Version verification failure: the version should only contain numbers and separator . characters", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateNormalizedVersionOk() {
		assertAll("valid versions",
				() -> assertDoesNotThrow(() -> validator.validateNormalizedVersion("1.2.3")),
				() -> assertDoesNotThrow(() -> validator.validateNormalizedVersion("1.0.145")),
				() -> assertDoesNotThrow(() -> validator.validateNormalizedVersion("0.02.3")));
	}
}