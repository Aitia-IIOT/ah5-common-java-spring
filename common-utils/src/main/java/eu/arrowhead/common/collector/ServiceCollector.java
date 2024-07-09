package eu.arrowhead.common.collector;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.model.ServiceModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
public class ServiceCollector {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private ICollectorDriver driver;

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	private Map<String, Object> arrowheadContext;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public ServiceModel getServiceModel(final String serviceDefinition, final String templateName) throws ArrowheadException {
		logger.debug("getServiceModel started...");

		final String key = Constants.KEY_PREFIX_FOR_SERVICE_MODEL + serviceDefinition;
		if (!arrowheadContext.containsKey(key)) {
			final ServiceModel model = driver.acquireService(serviceDefinition, templateName);
			arrowheadContext.put(key, model);
		}

		return (ServiceModel) arrowheadContext.get(key);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() throws ArrowheadException {
		driver.init();
	}
}
