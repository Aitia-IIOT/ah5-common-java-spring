package eu.arrowhead.common.service.validation.meta.evaluator;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class RegExpEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (left instanceof String && right instanceof String) {
			final String regexp = right.toString();
			return left.toString().matches(regexp);
		}

		return false;
	}
}