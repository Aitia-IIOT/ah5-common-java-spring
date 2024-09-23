package eu.arrowhead.common.service.validation.meta.evaluator;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class NotEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// members

	private final IMetaEvaluator evaluator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public NotEvaluator(final IMetaEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		return !evaluator.eval(left, right);
	}
}
