package eu.arrowhead.common.mqtt.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.http.HttpUtilities;
import eu.arrowhead.common.mqtt.ArrowheadMqttService;
import eu.arrowhead.common.mqtt.MqttResourceManager;
import eu.arrowhead.common.mqtt.MqttStatus;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttMessageContainer;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.dto.ErrorMessageDTO;
import eu.arrowhead.dto.MqttRequestTemplate;
import jakarta.servlet.http.HttpUpgradeHandler;

public abstract class MqttTopicHandler extends Thread {

	//=================================================================================================
	// members

	@Autowired
	protected ArrowheadMqttService ahMqttService;

	@Autowired
	protected ObjectMapper mapper;

	@Autowired
	private List<ArrowheadMqttFilter> filters;

	private BlockingQueue<MqttMessageContainer> queue;

	private boolean doWork = false;

	private final MqttResourceManager resourceManager = new MqttResourceManager();

	private ThreadPoolExecutor threadpool = null;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void init(final BlockingQueue<MqttMessageContainer> queue) {
		logger.debug("init started...");

		this.queue = queue;
		this.threadpool = resourceManager.getThreadpool();
		filters.sort((a, b) -> a.order() - b.order());
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run() {
		logger.debug("run started...");
		Assert.isTrue(queue != null, getClass().getName() + "is not initialized");

		doWork = true;
		while (doWork) {
			try {
				final MqttMessageContainer msgContainer = queue.take();

				try {
					threadpool.execute(() -> {
						final long startTime = System.currentTimeMillis();
						long endTime = 0;
						MqttRequestModel request = null;
						try {
							final Entry<String, MqttRequestModel> parsed = parseMqttMessage(msgContainer);
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
				} catch (final RejectedExecutionException ex) {
					Entry<String, MqttRequestModel> parsed = null;
					try {
						parsed = parseMqttMessage(msgContainer);
					} catch (final InvalidParameterException invalidEx) {
						logger.debug(invalidEx);
						errorResponse(ex, null);
						continue;
					}

					errorResponse(ex, parsed.getValue());
				}
			} catch (final InterruptedException ex) {
				logger.debug(ex.getMessage());
				logger.debug(ex);
			}
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void interrupt() {
		logger.debug("interrupt started...");

		doWork = false;
		super.interrupt();
	}

	//-------------------------------------------------------------------------------------------------
	public abstract String baseTopic();

	//-------------------------------------------------------------------------------------------------
	public abstract void handle(final MqttRequestModel request) throws ArrowheadException;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	protected void successResponse(final MqttRequestModel request, final MqttStatus status, final Object response) {
		logger.debug("successResponse started...");

		if (!Utilities.isEmpty(request.getResponseTopic())) {
			ahMqttService.response(
					request.getRequester(),
					request.getResponseTopic(),
					request.getTraceId(),
					request.getQosRequirement(),
					status,
					response);
		} else {
			logger.debug("No MQTT response topic was defined for success response");
		}
	}

	//-------------------------------------------------------------------------------------------------
	protected <T> T readPayload(final Object payload, final Class<T> dtoClass) {
		logger.debug("readPayload started...");

		if (payload == null) {
			return null;
		}

		if (dtoClass.isInstance(payload)) {
			return dtoClass.cast(payload);
		}

		try {
			return mapper.readValue(mapper.writeValueAsString(payload), dtoClass);
		} catch (final IOException ex) {
			throw new InvalidParameterException("Could not parse payload. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	protected <T> T readPayload(final Object payload, final TypeReference<T> dtoTypeRef) {
		logger.debug("readPayload started...");

		if (payload == null) {
			return null;
		}

		try {
			return mapper.readValue(mapper.writeValueAsString(payload), dtoTypeRef);
		} catch (final IOException ex) {
			throw new InvalidParameterException("Could not parse payload. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private Pair<String, MqttRequestModel> parseMqttMessage(final MqttMessageContainer msgContainer) {
		logger.debug("parseMqttMessage started...");
		Assert.notNull(msgContainer, "MqttMessageContainer is null");

		if (msgContainer.getMessage() == null) {
			throw new InvalidParameterException("Invalid message template: null message");
		}

		try {
			final MqttRequestTemplate template = mapper.readValue(msgContainer.getMessage().getPayload(), MqttRequestTemplate.class);
			return new ImmutablePair<String, MqttRequestModel>(template.authentication(), new MqttRequestModel(msgContainer.getBaseTopic(), msgContainer.getOperation(), template));
		} catch (final IOException ex) {
			throw new InvalidParameterException("Invalid message template. Reason: " + ex.getMessage());
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void errorResponse(final Exception ex, final MqttRequestModel request) {
		logger.debug("errorResponse started...");

		if (request == null) {
			logger.error("MQTT error occured, without request model being parsed");
			logger.debug(ex);
			return;
		}

		if (Utilities.isEmpty(request.getResponseTopic())) {
			logger.error("MQTT request error occured, but no response topic has been defined");
			logger.debug(ex);
			return;
		}

		final MqttStatus status = calculateStatusFromException(ex);
		
		final ErrorMessageDTO dto = HttpUtilities.createErrorMessageDTO(new ArrowheadException(ex.getMessage(), request.getBaseTopic() + request.getOperation()));
		
		ahMqttService.response(
				request.getRequester(),
				request.getResponseTopic(),
				request.getTraceId(),
				request.getQosRequirement(),
				status,
				dto);
	}

	//-------------------------------------------------------------------------------------------------
	private MqttStatus calculateStatusFromException(final Exception ex) {
		logger.debug("calculateStatusFromException started...");

		if (!(ex instanceof ArrowheadException)) {
			return MqttStatus.INTERNAL_SERVER_ERROR;
		}

		final ArrowheadException ahEx = (ArrowheadException) ex;
		MqttStatus status = MqttStatus.resolve(ahEx.getExceptionType().getErrorCode());
		if (status == null) {
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
			case LOCKED:
				status = MqttStatus.LOCKED;
				break;
			default:
				status = MqttStatus.INTERNAL_SERVER_ERROR;
			}
		}

		return status;
	}
}