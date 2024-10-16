package eu.arrowhead.common.mqtt.handler;

import java.io.IOException;
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

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.mqtt.ArrowheadMqttService;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.dto.MqttRequestTemplate;

public abstract class MqttTopicHandler extends Thread {

	//=================================================================================================
	// members

	@Autowired
	protected ArrowheadMqttService ahMqttService;

	@Autowired
	private ObjectMapper mapper;

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

						// Filter chain
						request = authenticate(parsed);
						authorize(request);

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
	public abstract void handle(final MqttRequestModel request);

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Entry<String, MqttRequestModel> parseMqttMessage(final MqttMessage message) {
		try {
			final MqttRequestTemplate template = mapper.readValue(message.getPayload(), MqttRequestTemplate.class);
			// TODO validate template
			return Map.entry(template.authentication(), new MqttRequestModel(template));
		} catch (final IOException ex) {
			throw new InvalidParameterException("Invalid message template. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private MqttRequestModel authenticate(final Entry<String, MqttRequestModel> parsed) {
		final MqttRequestModel request = parsed.getValue();

		System.out.println("authenticate traceId: " + request.getTraceId());
		//TODO set requester after auth, also isSysop
		return request;
	}

	//-------------------------------------------------------------------------------------------------
	private void authorize(final MqttRequestModel request) {
		//TODO throw exception if forbidden
		System.out.println("authorize traceId: " + request.getTraceId());
	}

	//-------------------------------------------------------------------------------------------------
	private void errorResponse(final Exception ex, final MqttRequestModel request) {
		//TODO
		System.out.println("error traceId: " + request.getTraceId() + ". Origin: " + topic());
	}
}
