package eu.arrowhead.common.intf.properties;

import eu.arrowhead.common.intf.properties.validators.HttpOperationsValidator;
import eu.arrowhead.common.intf.properties.validators.MinMaxValidator;
import eu.arrowhead.common.intf.properties.validators.NotEmptyAddressListValidator;

public enum PropertyValidators {
	MINMAX(new MinMaxValidator()),
	NOT_EMPTY_ADDRESS_LIST(new NotEmptyAddressListValidator()),
	HTTP_OPERATIONS(new HttpOperationsValidator());

	//=================================================================================================
	// members

	private final IPropertyValidator validator;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private PropertyValidators(final IPropertyValidator validator) {
		this.validator = validator;
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public IPropertyValidator getValidator() {
		return validator;
	}
}
