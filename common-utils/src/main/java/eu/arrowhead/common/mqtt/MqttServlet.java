package eu.arrowhead.common.mqtt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.mqtt.handler.MqttTopicHandler;
import jakarta.annotation.PostConstruct;

@Component
public class MqttServlet {

	//=================================================================================================
	//members

	@Autowired
	private List<MqttTopicHandler> handlers;

	private final Map<String, BlockingQueue<MqttMessage>> topicQueueMap = new HashMap<>();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	protected void addTopic(final String topic) {
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		if (topicQueueMap.containsKey(topic)) {
			return;
		}

		final Optional<MqttTopicHandler> handlerOpt = handlers.stream().filter(h -> h.topic().equals(topic)).findFirst();
		if (handlerOpt.isEmpty()) {
			Assert.isTrue(false,  "No service handler exists for topic: " + topic);
		}

		topicQueueMap.put(topic, new LinkedBlockingQueue<>());
		handlerOpt.get().init(topicQueueMap.get(topic));
		handlerOpt.get().start();
	}

	//-------------------------------------------------------------------------------------------------
	protected void queueMessage(final String topic, final MqttMessage msg) {
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");
		Assert.isTrue(topicQueueMap.containsKey(topic), "unknown topic");

		topicQueueMap.get(topic).add(msg);

	}

	//-------------------------------------------------------------------------------------------------
	protected Set<String> getTopicSet() {
		return topicQueueMap.keySet();
	}

	//-------------------------------------------------------------------------------------------------
	protected boolean handlerExists(final String topic) {
		return handlers.stream().filter(h -> h.topic().equals(topic)).findFirst().isPresent();
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		addTopic(Constants.MQTT_TOPIC_UNSUPPORTED);
	}
}
