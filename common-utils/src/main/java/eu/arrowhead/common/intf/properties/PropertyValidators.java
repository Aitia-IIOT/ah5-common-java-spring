package eu.arrowhead.common.intf.properties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.intf.properties.validators.HttpOperationsValidator;
import eu.arrowhead.common.intf.properties.validators.MinMaxValidator;
import eu.arrowhead.common.intf.properties.validators.NotEmptyAddressListValidator;
import eu.arrowhead.common.intf.properties.validators.PortValidator;
import jakarta.annotation.PostConstruct;

@Service
public class PropertyValidators {

	//=================================================================================================
	// members

	private final Map<PropertyValidatorType, IPropertyValidator> validators = new ConcurrentHashMap<>();

	@Autowired
	private ApplicationContext appContext;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public IPropertyValidator getValidator(final PropertyValidatorType type) {
		return validators.get(type);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		validators.put(PropertyValidatorType.MINMAX, appContext.getBean(MinMaxValidator.class));
		validators.put(PropertyValidatorType.PORT, appContext.getBean(PortValidator.class));
		validators.put(PropertyValidatorType.NOT_EMPTY_ADDRESS_LIST, appContext.getBean(NotEmptyAddressListValidator.class));
		validators.put(PropertyValidatorType.HTTP_OPERATIONS, appContext.getBean(HttpOperationsValidator.class));
	}
}
