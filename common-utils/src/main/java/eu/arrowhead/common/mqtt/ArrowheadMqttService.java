package eu.arrowhead.common.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ExternalServerError;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.dto.MqttResponseTemplate;

@Service
public class ArrowheadMqttService {

	//=================================================================================================
	// members

	@Autowired
	private MqttService mqttService;

	@Autowired
	private ObjectMapper mapper;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	/**
	 * Subscribe for consuming a push service
	 */
	public void subscribe(final String connectId, final String topic, final MqttQoS qos, final Runnable handler) {
		logger.debug("subscribe started");
		Assert.isTrue(!Utilities.isEmpty(connectId), "connectId is empty");
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		// TODO
	}

	//-------------------------------------------------------------------------------------------------
	/**
	 * Subscribe for consuming a push service
	 */
	public void unsubscribe(final String connectId, final String[] topics) {
		logger.debug("unsubscribe started");
		Assert.isTrue(!Utilities.isEmpty(connectId), "connectId is empty");
		Assert.notNull(topics, "topic array is null");

		final MqttClient client = mqttService.client(connectId);
		Assert.notNull(client, "Unknown connectId: " + connectId);

		try {
			client.unsubscribe(topics);
		} catch (final MqttException ex) {
			logger.debug(ex);
			throw new ExternalServerError("MQTT unsubscribe failed: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	/**
	 * Publish a service message
	 */
	public void publish(final String connectId, final String sender, final String topic, final MqttQoS qos, final Object payload) {
		logger.debug("publish started");
		Assert.isTrue(!Utilities.isEmpty(connectId), "connectId is empty");
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		// TODO needs an MqttPublishTemplate ()
	}

	//-------------------------------------------------------------------------------------------------
	/**
	 * Publish a response for a pull (request-response) service when it is provided via MQTT
	 */
	public void response(final String connectId, final String receiver, final String topic, final String traceId, final MqttQoS qos, final MqttStatus status, final Object payload) {
		logger.debug("response started");
		Assert.isTrue(!Utilities.isEmpty(connectId), "connectId is empty");
		Assert.isTrue(!Utilities.isEmpty(receiver), "receiver is empty");
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		final MqttClient client = mqttService.client(connectId);
		Assert.notNull(client, "Unknown connectId: " + connectId);

		try {
			final MqttResponseTemplate template = new MqttResponseTemplate(status.value(), traceId, receiver, payload == null ? "" : payload);
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
}
