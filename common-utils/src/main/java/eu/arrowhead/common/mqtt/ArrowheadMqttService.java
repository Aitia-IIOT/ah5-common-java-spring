package eu.arrowhead.common.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.collector.ServiceCollector;
import eu.arrowhead.common.model.ServiceModel;
import jakarta.annotation.PostConstruct;

@Service
public class ArrowheadMqttService {

	//=================================================================================================
	// members

	@Autowired
	private ServiceCollector collector;

	@Autowired
	private MqttService mqttService;

	@Autowired
	private SystemInfo sysInfo;

	private String templateName;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public <T, P> T serviceSubscribe(final String serviceDefinition, final String operation, final Class<T> responseType) {
		logger.debug("serviceSubscribe started");

		ServiceModel serviceModel = collector.getServiceModel(serviceDefinition, templateName);

		// TODO
		return null;
	}

	//-------------------------------------------------------------------------------------------------
	public void servicePublish() {
		//TODO
	}
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		templateName = sysInfo.isSslEnabled() ? Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME;
	}
}
