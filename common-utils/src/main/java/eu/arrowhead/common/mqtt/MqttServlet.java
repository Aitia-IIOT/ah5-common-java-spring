package eu.arrowhead.common.mqtt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;
import jakarta.annotation.PostConstruct;

@Component
public class MqttServlet {

	//=================================================================================================
	//members

	private final String invalidTopic = UUID.randomUUID().toString();

	private final Map<String, BlockingQueue<MqttMessage>> topicQueueMap = new HashMap<>();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	protected void addTopic(final String topic) {
		Assert.isTrue(!Utilities.isEmpty(topic), "topic is empty");

		topicQueueMap.putIfAbsent(topic, new LinkedBlockingQueue<>());
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
	protected BlockingQueue<MqttMessage> getInvalidTopicQueue() {
		return topicQueueMap.get(invalidTopic);
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	private void init() {
		addTopic(invalidTopic);
	}
}
