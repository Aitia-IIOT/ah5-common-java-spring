package eu.arrowhead.common.mqtt.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.mqtt.ArrowheadMqttService;
import eu.arrowhead.common.mqtt.MqttResourceManager;
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

	private boolean doWork = false;

	private final MqttResourceManager resourceManager = new MqttResourceManager();

	private ThreadPoolExecutor threadpool = null;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	public void init(final BlockingQueue<MqttMessage> queue) {
		this.queue = queue;
		this.threadpool = resourceManager.getThreadpool();
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
					long startTime = System.currentTimeMillis();
					long endTime = 0;
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
					} finally {
						endTime = System.currentTimeMillis();
						resourceManager.registerLatency(endTime - startTime);
					}
				});

			} catch (final InterruptedException ex) {
				logger.debug(ex.getMessage());
				logger.debug(ex);
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
		} else {
			logger.debug("No MQTT response topic was defined for success response.");
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
			return Map.entry(template.authentication(), new MqttRequestModel(topic(), template));
		} catch (final IOException ex) {
			throw new InvalidParameterException("Invalid message template. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void errorResponse(final Exception ex, final MqttRequestModel request) {
		if (request == null) {
			logger.error("MQTT error occured, before request model parsing");
			logger.debug(ex);
			return;
		}

		if (Utilities.isEmpty(request.getResponseTopic())) {
			logger.error("MQTT request error occured, but no response topic has been defined.");
			logger.debug(ex);
			return;
		}

		final MqttStatus status = calculateStatusFromException(ex);
		ahMqttService.response(Constants.MQTT_SERVICE_PROVIDING_BROKER_CONNECT_ID, request.getRequester(), request.getResponseTopic(), request.getTraceId(), request.getQosRequirement(), status, ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	private MqttStatus calculateStatusFromException(final Exception ex) {
		if (!(ex instanceof ArrowheadException)) {
			return MqttStatus.INTERNAL_SERVER_ERROR;
		}

		final ArrowheadException ahEx = (ArrowheadException) ex;
		MqttStatus status = MqttStatus.resolve(ahEx.getExceptionType().getErrorCode());
		if (status == null) {
			// TODO LockedException!
			switch (ahEx.getExceptionType()) {
			case AUTH:
				status = MqttStatus.UNAUTHORIZED;
				break;
			case FORBIDDEN:
				status = MqttStatus.FORBIDDEN;
				break;
			case INVALID_PARAMETER:
				status = MqttStatus.BAD_REQUEST;
				break;
			case DATA_NOT_FOUND:
				status = MqttStatus.NOT_FOUND;
				break;
			case EXTERNAL_SERVER_ERROR:
				status = MqttStatus.EXTERNAL_SERVER_ERROR;
				break;
			case TIMEOUT:
				status = MqttStatus.TIMEOUT;
				break;
			default:
				status = MqttStatus.INTERNAL_SERVER_ERROR;
			}

		}
		return null;
	}
}
