package eu.arrowhead.common.mqtt.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.mqtt.ArrowheadMqttService;
import eu.arrowhead.common.mqtt.MqttStatus;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.dto.MqttRequestTemplate;

public abstract class MqttTopicHandler extends Thread {

	//=================================================================================================
	// members

	@Autowired
	protected ArrowheadMqttService ahMqttService;

	@Autowired
	protected ObjectMapper mapper;

	@Autowired
	private List<ArrowheadMqttFilter> filters;

	private BlockingQueue<MqttMessage> queue;

	private ThreadPoolExecutor threadpool = null;

	private boolean doWork = false;

	//=================================================================================================
	// methods

	public void init(final BlockingQueue<MqttMessage> queue) {
		this.queue = queue;
		threadpool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		threadpool.setCorePoolSize(1);
		threadpool.setMaximumPoolSize(100); // TODO calculate these
		threadpool.setKeepAliveTime(30, TimeUnit.SECONDS);

		filters.sort((a, b) -> a.order() - b.order());
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		Assert.isTrue(queue != null, getClass().getName() + "is not initialized");

		doWork = true;
		while (doWork) {
			try {
				final MqttMessage message = queue.take();
				threadpool.execute(() -> {
					MqttRequestModel request = null;
					try {
						final Entry<String, MqttRequestModel> parsed = parseMqttMessage(message);
						request = parsed.getValue();

						// Filter chain
						for (final ArrowheadMqttFilter filter : filters) {
							filter.doFilter(parsed.getKey(), request);
						}

						// API call
						handle(request);
					} catch (final Exception ex) {
						errorResponse(ex, request);
					}
				});

			} catch (final InterruptedException e) {
				// TODO
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void interrupt() {
		doWork = false;
		super.interrupt();
	}

	//-------------------------------------------------------------------------------------------------
	public abstract String topic();

	//-------------------------------------------------------------------------------------------------
	public abstract void handle(final MqttRequestModel request) throws ArrowheadException;

	//-------------------------------------------------------------------------------------------------
	protected void successResponse(final MqttRequestModel request, final MqttStatus status, final Object response) {
		if (!Utilities.isEmpty(request.getResponseTopic())) {
			ahMqttService.response(Constants.MQTT_SERVICE_PROVIDING_BROKER_CONNECT_ID, request.getRequester(), request.getResponseTopic(), request.getTraceId(), request.getQosRequirement(), status, response);
		}
	}

	//-------------------------------------------------------------------------------------------------
	protected <T> T readPayload(final Object payload, final Class<T> dtoClass) {
		if (payload == null) {
			return null;
		}

		try {
			return mapper.readValue(mapper.writeValueAsString(payload), dtoClass);
		} catch (final IOException ex) {
			throw new InvalidParameterException("Coud not parse payload. Reason: " + ex.getMessage());
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Entry<String, MqttRequestModel> parseMqttMessage(final MqttMessage message) {
		try {
			final MqttRequestTemplate template = mapper.readValue(message.getPayload(), MqttRequestTemplate.class);
			// TODO validate template
			return Map.entry(template.authentication(), new MqttRequestModel(topic(), template));
		} catch (final IOException ex) {
			throw new InvalidParameterException("Invalid message template. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void errorResponse(final Exception ex, final MqttRequestModel request) {
		if (request == null) {
			System.out.println(ex.getMessage() + " Origin: " + topic());
			return;
		}

		final MqttStatus status = MqttStatus.INTERNAL_SERVER_ERROR; // TODO calculate from ex;
		if (!Utilities.isEmpty(request.getResponseTopic())) {
			ahMqttService.response(eu.arrowhead.common.Constants.MQTT_SERVICE_PROVIDING_BROKER_CONNECT_ID, request.getRequester(), request.getResponseTopic(), request.getTraceId(), request.getQosRequirement(), status, ex.getMessage());
		}
	}
}
