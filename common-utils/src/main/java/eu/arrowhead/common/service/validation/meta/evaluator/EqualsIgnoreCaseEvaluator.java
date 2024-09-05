package eu.arrowhead.common.service.validation.meta.evaluator;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class EqualsIgnoreCaseEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (left instanceof String && right instanceof String) {
			return left.toString().toLowerCase().equals(right.toString().toLowerCase());
		}

		return false;
	}
}
