package eu.arrowhead.common.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.collector.ServiceCollector;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.mqtt.model.MqttInterfaceModel;
import eu.arrowhead.dto.MqttPublishTemplate;
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
	private MqttServlet mqttServlet;

	@Autowired
	private SystemInfo sysInfo;

	@Autowired
	private ObjectMapper mapper;

	private String templateName;

	private final MqttClient client = null;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void serviceListener(final String serviceDefinition) {
		logger.debug("serviceListener started");
		Assert.isTrue(!Utilities.isEmpty(serviceDefinition), "serviceDefinition is empty");

		final ServiceModel model = collector.getServiceModel(serviceDefinition, templateName);
		serviceListener(model);
	}

	//-------------------------------------------------------------------------------------------------
	public void serviceListener(final ServiceModel model) {
		logger.debug("serviceListener started");
		Assert.notNull(model, "ServiceModel is null");

		final MqttInterfaceModel interfaceModel = (MqttInterfaceModel) model.interfaces().stream().filter(i -> i.templateName().equals(templateName)).toList().getFirst();

		try {
			if (client == null) {
				initMqttClient(interfaceModel.accessAddresses().getFirst(), interfaceModel.accessPort());
			}
			mqttServlet.addTopic(interfaceModel.topic());

		} catch (final MqttException ex) {
			logger.debug(ex);
			throw new ExternalServerError("MQTT service listener creation failed: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	public void serviceResponser(final String receiver, final String topic, final String traceId, final MqttQoS qos, final boolean success, final Object payload) {
		logger.debug("serviceResponser started");
		Assert.notNull(client, "client is null");
		Assert.isTrue(!Utilities.isEmpty(receiver), "receiver is empty");
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		try {
			final MqttPublishTemplate template = new MqttPublishTemplate(success, traceId, receiver, payload == null ? "" : payload);
			final MqttMessage msg = new MqttMessage(mapper.writeValueAsBytes(template));
			msg.setQos(qos == null ? MqttQoS.AT_MOST_ONCE.value() : qos.value());
			client.publish(topic, msg);

		} catch (final JsonProcessingException ex) {
			logger.debug(ex);
			throw new InternalServerError("MQTT service response message creation failed: " + ex.getMessage());

		} catch (final MqttException ex) {
			logger.debug(ex);
			throw new ExternalServerError("MQTT service response failed: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	public void disconnect() {
		logger.debug("disconnect started");
		Assert.notNull(client, "client is null");

		try {
			final String[] topics = mqttServlet.getTopicSet().toArray(new String[0]);
			client.unsubscribe(topics);
			client.disconnect();

		} catch (final MqttException ex) {
			logger.debug(ex);
			throw new ExternalServerError("Disconnecting MQTT Broker failed: " + ex.getMessage());
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		templateName = sysInfo.isSslEnabled() ? Constants.GENERIC_MQTTS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_MQTT_INTERFACE_TEMPLATE_NAME;
	}

	//-------------------------------------------------------------------------------------------------
	private MqttClient initMqttClient(final String address, final int port) throws MqttException {
		final MqttClient client = mqttService.createConnection(address, port, "AH-" + sysInfo.getSystemName(), sysInfo.getSystemName(), sysInfo.getMqttClientPassword());
		client.connect();
		client.setCallback(createMqttCallback(client.getServerURI()));

		return client;
	}

	//-------------------------------------------------------------------------------------------------
	private MqttCallback createMqttCallback(final String brokerUri) {
		return new MqttCallback() {

			@Override
			public void messageArrived(final String topic, final MqttMessage message) throws Exception {
				logger.debug("MQTT message arrived to service topic: " + topic);
				mqttServlet.queueMessage(topic, message);
			}

			@Override
			public void deliveryComplete(final IMqttDeliveryToken token) {
				logger.debug("MQTT message delivered to broker " + brokerUri + ". Topic(s): " + token.getTopics());
			}

			@Override
			public void connectionLost(final Throwable cause) {
				logger.error("MQTT Broker connection lost: " + brokerUri + ". Reason: " + cause.getMessage());
			}
		};
	}
}
