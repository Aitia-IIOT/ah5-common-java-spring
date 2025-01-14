package eu.arrowhead.common.mqtt;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.mqtt.handler.MqttTopicHandler;

@Component
@ConditionalOnProperty(name = Constants.MQTT_API_ENABLED, matchIfMissing = false)
public class MqttDispatcher {

	//=================================================================================================
	//members

	@Autowired
	private List<MqttTopicHandler> handlers;

	private final Map<String, BlockingQueue<MqttMessage>> topicQueueMap = new ConcurrentHashMap<>();

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	protected void addTopic(final String topic) {
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		if (topicQueueMap.containsKey(topic)) {
			return;
		}

		final Optional<MqttTopicHandler> handlerOpt = handlers.stream().filter(h -> h.topic().equals(topic)).findFirst();
		if (handlerOpt.isEmpty()) {
			throw new IllegalArgumentException("No service handler exists for topic: " + topic);
		}

		topicQueueMap.put(topic, new LinkedBlockingQueue<>());
		handlerOpt.get().init(topicQueueMap.get(topic));
		handlerOpt.get().start();
	}

	//-------------------------------------------------------------------------------------------------
	protected void revokeTopic(final String topic) {
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		if (!topicQueueMap.containsKey(topic)) {
			return;
		}

		topicQueueMap.remove(topic);
		final Optional<MqttTopicHandler> handlerOpt = handlers.stream().filter(h -> h.topic().equals(topic)).findFirst();
		if (handlerOpt.isPresent()) {
			handlerOpt.get().interrupt();
		}
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
		return handlers.stream().anyMatch(h -> h.topic().equals(topic));
	}
}