package eu.arrowhead.common.service.validation.meta.evaluator;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class LessThanOrEqualsToEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (left instanceof final Number leftNum && right instanceof final Number rightNum) {
			final boolean leftIsReal = (left instanceof Double || left instanceof Float);
			final boolean rightIsReal = (right instanceof Double || right instanceof Float);

			if (leftIsReal) {
				return leftNum.doubleValue() <= (rightIsReal ? rightNum.doubleValue() : rightNum.longValue());
			}

			return leftNum.longValue() <= (rightIsReal ? rightNum.doubleValue() : rightNum.longValue());
		}

		return false;
	}
}