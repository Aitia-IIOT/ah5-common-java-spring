package eu.arrowhead.common.service.validation.meta.evaluator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class TextOperationsTest {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void evalEqualsIgnoreCase() {
		final IMetaEvaluator evaluator = new EqualsIgnoreCaseEvaluator();

		assertAll("Equals ignore case tests",
				() -> assertFalse(evaluator.eval(null, null)),
				() -> assertFalse(evaluator.eval(null, "text")),
				() -> assertFalse(evaluator.eval("text", null)),
				() -> assertFalse(evaluator.eval(10, 10)),
				() -> assertTrue(evaluator.eval("text", "text")),
				() -> assertTrue(evaluator.eval("TEXT", "text")),
				() -> assertTrue(evaluator.eval("text", "TEXT")),
				() -> assertTrue(evaluator.eval("Text", "texT")),
				() -> assertFalse(evaluator.eval("TEXT", "test")));
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	@SuppressWarnings("checkstyle:magicnumber")
	public void evalIncludes() {
		final IMetaEvaluator evaluator = new IncludesEvaluator();

		assertAll("Includes case tests",
				() -> assertFalse(evaluator.eval(null, null)),
				() -> assertFalse(evaluator.eval(null, "text")),
				() -> assertFalse(evaluator.eval("text", null)),
				() -> assertFalse(evaluator.eval(10, 10)),
				() -> assertTrue(evaluator.eval("text", "text")),
				() -> assertTrue(evaluator.eval("longtext", "text")),
				() -> assertTrue(evaluator.eval("textlong", "text")),
				() -> assertTrue(evaluator.eval("longtextlong", "text")),
				() -> assertFalse(evaluator.eval("longtext", "test")));
	}
	
	// TODO: continue
}
