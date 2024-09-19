package eu.arrowhead.common.service.validation.meta.evaluator;

import java.util.List;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class SizeEqualsEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (!(right instanceof Number)) {
			return false;
		}

		final int size = ((Number) right).intValue();

		if (left instanceof String) {
			return left.toString().length() == size;
		}

		if (left instanceof List<?>) {
			return ((List<?>) left).size() == size;
		}

		return false;
	}
}
