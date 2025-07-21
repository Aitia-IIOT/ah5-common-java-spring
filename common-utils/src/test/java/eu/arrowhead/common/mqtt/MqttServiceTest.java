package eu.arrowhead.common.mqtt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.SSLProperties;

@ExtendWith(MockitoExtension.class)
public class MqttServiceTest {

	//=================================================================================================
	// members

	@InjectMocks
	private MqttService service;

	@Mock
	private SSLProperties sslProperties;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testClientInputNull() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> service.client(null));

		assertEquals("connectId is empty", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testClientInputEmpty() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> service.client(""));

		assertEquals("connectId is empty", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testClientUnknownId() {
		assertNull(service.client("unknownClientId"));
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	@Test
	public void testClientKnownId() {
		final Map<String, MqttClient> clientMap = (Map<String, MqttClient>) ReflectionTestUtils.getField(service, "clientMap");
		final MqttClient clientMock = Mockito.mock(MqttClient.class);
		clientMap.put("knownClientId", clientMock);

		final MqttClient result = service.client("knownClientId");
		assertEquals(clientMock, result);
	}
}
