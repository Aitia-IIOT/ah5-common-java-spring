package eu.arrowhead.common.service.validation.meta.evaluator;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class ListNotContainsEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// members

	private static IMetaEvaluator positiveEvaluator = new ListContainsEvaluator();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		return !positiveEvaluator.eval(left, right);
	}
}
