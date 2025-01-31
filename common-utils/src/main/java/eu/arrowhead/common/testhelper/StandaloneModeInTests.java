package eu.arrowhead.common.testhelper;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import eu.arrowhead.common.Constants;
import jakarta.annotation.Resource;

public abstract class StandaloneModeInTests {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Resource(name = Constants.ARROWHEAD_CONTEXT)
	private Map<String, Object> arrowheadContext;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@EventListener
	@Order(5)
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		logger.info("STANDALONE mode is set...");
		arrowheadContext.put(Constants.SERVER_STANDALONE_MODE, true);
	}
}