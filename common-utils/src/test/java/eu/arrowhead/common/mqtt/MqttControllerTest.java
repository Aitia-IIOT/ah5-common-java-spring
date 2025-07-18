package eu.arrowhead.common.mqtt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.common.mqtt.model.MqttInterfaceModel;

@ExtendWith(MockitoExtension.class)
public class MqttControllerTest {

	//=================================================================================================
	// members

	@InjectMocks
	private MqttController controller;

	@Mock
	private MqttService mqttService;

	@Mock
	private MqttDispatcher mqttDispatcher;

	@Mock
	private SystemInfo sysInfo;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testListenInputNull() {
		final Throwable ex = assertThrows(IllegalArgumentException.class,
				() -> controller.listen(null));

		assertEquals("ServiceModel is null", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testListenInterfaceMismatch() throws MqttException {
		ReflectionTestUtils.setField(controller, "templateName", "generic_mqtt");

		final ServiceModel serviceDiscoverySM = new ServiceModel.Builder()
				.serviceDefinition("serviceDiscovery")
				.version("5.0.0")
				.serviceInterface(new MqttInterfaceModel.Builder("generic_mqtts", "localhost", 4763)
						.baseTopic("arrowhead/serviceregistry/service-discovery/")
						.operations(Set.of("register", "lookup", "revoke"))
						.build())
				.build();

		assertDoesNotThrow(() -> controller.listen(serviceDiscoverySM));
		assertNull(ReflectionTestUtils.getField(controller, "client"));

		verify(mqttService, never()).connect(anyString(), anyString(), anyInt(), anyBoolean());
	}
}
