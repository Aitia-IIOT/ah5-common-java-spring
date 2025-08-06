package eu.arrowhead.common.mqtt;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MagicNumber")
public class MqttStatusTest {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testResolve() {
		assertAll("normal cases",
				() -> assertEquals(MqttStatus.OK, MqttStatus.resolve(200)),
				() -> assertEquals(MqttStatus.CREATED, MqttStatus.resolve(201)),
				() -> assertEquals(MqttStatus.NO_CONTENT, MqttStatus.resolve(204)),
				() -> assertEquals(MqttStatus.BAD_REQUEST, MqttStatus.resolve(400)),
				() -> assertEquals(MqttStatus.UNAUTHORIZED, MqttStatus.resolve(401)),
				() -> assertEquals(MqttStatus.FORBIDDEN, MqttStatus.resolve(403)),
				() -> assertEquals(MqttStatus.NOT_FOUND, MqttStatus.resolve(404)),
				() -> assertEquals(MqttStatus.TIMEOUT, MqttStatus.resolve(408)),
				() -> assertEquals(MqttStatus.LOCKED, MqttStatus.resolve(423)),
				() -> assertEquals(MqttStatus.INTERNAL_SERVER_ERROR, MqttStatus.resolve(500)),
				() -> assertEquals(MqttStatus.EXTERNAL_SERVER_ERROR, MqttStatus.resolve(503)),
				() -> assertNull(MqttStatus.resolve(1000)));
	}
}