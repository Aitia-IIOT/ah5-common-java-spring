package eu.arrowhead.common.mqtt.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.mqtt.MqttResourceManager;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttMessageContainer;

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
}
