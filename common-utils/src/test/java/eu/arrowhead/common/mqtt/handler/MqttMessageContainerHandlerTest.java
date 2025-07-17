package eu.arrowhead.common.mqtt.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.mqtt.MqttResourceManager;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.handler.MqttTopicHandlerTest.DummyArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttMessageContainer;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;
import eu.arrowhead.dto.MqttRequestTemplate;

@ExtendWith(MockitoExtension.class)
public class MqttMessageContainerHandlerTest {

	//=================================================================================================
	// members

	@Mock
	private MqttHandlerUtils utils;

	@Mock
	private List<ArrowheadMqttFilter> filters;

	@Mock
	private MqttMessageContainer msgContainer;

	@Mock
	private MqttTopicHandler topicHandler;

	@Mock
	private MqttResourceManager resourceManager;

	@InjectMocks
	private MqttMessageContainerHandler handler = new MqttMessageContainerHandler(topicHandler, msgContainer, resourceManager);

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(handler, "msgContainer", msgContainer);
		ReflectionTestUtils.setField(handler, "topicHandler", topicHandler);
		ReflectionTestUtils.setField(handler, "resourceManager", resourceManager);
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testRunMsgContainerNull() {
		ReflectionTestUtils.setField(handler, "msgContainer", null);
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> handler.run());

		assertEquals("msgContainer is null", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testRunTopicHandlerNull() {
		ReflectionTestUtils.setField(handler, "topicHandler", null);
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> handler.run());

		assertEquals("topicHandler is null", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testRunResourceManagerNull() {
		ReflectionTestUtils.setField(handler, "resourceManager", null);
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> handler.run());

		assertEquals("resourceManager is null", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MagicNumber")
	@Test
	public void testRunOk() {
		ReflectionTestUtils.setField(handler, "filters", List.of(new DummyArrowheadMqttFilter(10)));
		when(utils.parseMqttMessage(any(MqttMessageContainer.class))).thenReturn(new ImmutablePair<String, MqttRequestModel>(
				"test",
				new MqttRequestModel(
						"test-base",
						"test-operation",
						new MqttRequestTemplate("trace", "auth", "response", 0, null, null))));
		doNothing().when(topicHandler).handle(any(MqttRequestModel.class));
		doNothing().when(resourceManager).registerLatency(anyLong());

		assertDoesNotThrow(() -> handler.run());

		verify(utils).parseMqttMessage(any(MqttMessageContainer.class));
		verify(topicHandler).handle(any(MqttRequestModel.class));
		verify(resourceManager).registerLatency(anyLong());
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("checkstyle:MagicNumber")
	@Test
	public void testRunExceptionDuringHandle() {
		ReflectionTestUtils.setField(handler, "filters", List.of(new DummyArrowheadMqttFilter(10)));
		when(utils.parseMqttMessage(any(MqttMessageContainer.class))).thenReturn(new ImmutablePair<String, MqttRequestModel>(
				"test",
				new MqttRequestModel(
						"test-base",
						"test-operation",
						new MqttRequestTemplate("trace", "auth", "response", 0, null, null))));
		doThrow(ArrowheadException.class).when(topicHandler).handle(any(MqttRequestModel.class));
		doNothing().when(utils).errorResponse(any(ArrowheadException.class), any(MqttRequestModel.class));
		doNothing().when(resourceManager).registerLatency(anyLong());

		assertDoesNotThrow(() -> handler.run());

		verify(utils).parseMqttMessage(any(MqttMessageContainer.class));
		verify(topicHandler).handle(any(MqttRequestModel.class));
		verify(utils).errorResponse(any(ArrowheadException.class), any(MqttRequestModel.class));
		verify(resourceManager).registerLatency(anyLong());
	}
}