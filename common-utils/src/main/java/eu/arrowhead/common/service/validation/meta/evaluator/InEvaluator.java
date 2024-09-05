package eu.arrowhead.common.service.validation.meta.evaluator;

import java.util.List;

import eu.arrowhead.common.service.validation.meta.IMetaEvaluator;

public class InEvaluator implements IMetaEvaluator {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean eval(final Object left, final Object right) {
		if (right instanceof List<?>) {
			return ((List<?>) right).contains(left);
		}

		return false;
	}
}
