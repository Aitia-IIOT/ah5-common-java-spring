package eu.arrowhead.common.service.validation.meta.evaluator;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class LessThanOrEqualsToEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (left instanceof Number && right instanceof Number) {
			final Number leftNum = (Number) left;
			final Number rightNum = (Number) right;
			boolean leftIsReal = (left instanceof Double || left instanceof Float);
			boolean rightIsReal = (right instanceof Double || right instanceof Float);

			if (leftIsReal) {
				return leftNum.doubleValue() <= (rightIsReal ? rightNum.doubleValue() : rightNum.longValue());
			}

			return leftNum.longValue() <= (rightIsReal ? rightNum.doubleValue() : rightNum.longValue());
		}

		return false;
	}
}
