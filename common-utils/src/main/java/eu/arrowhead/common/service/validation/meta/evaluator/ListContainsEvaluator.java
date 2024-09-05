package eu.arrowhead.common.service.validation.meta.evaluator;

import java.util.List;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class ListContainsEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (left instanceof List<?>) {
			return ((List<?>) left).contains(right);
		}

		return false;
	}
}
