package eu.arrowhead.common.mqtt;

import java.util.UUID;

import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

@Component
public class MqttService {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public MqttClient createConnection(final String address, final int port, final SSLSocketFactory sslSocketFactory, final String clientId, final String username, final String password) throws MqttException {
		logger.debug("createConnection started");
		Assert.isTrue(!Utilities.isEmpty(address), "address is empty");

		String serverURI = sslSocketFactory == null ? "tcp://" : "ssl://";
		serverURI = serverURI + address + ":" + port;

		final MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		if (!Utilities.isEmpty(username)) {
			options.setUserName(username);
		}
		if (!Utilities.isEmpty(password)) {
			options.setPassword(password.toCharArray());
		}
		if (sslSocketFactory != null) {
			options.setSocketFactory(sslSocketFactory);
		}

		final MqttClient client = new MqttClient(serverURI, !Utilities.isEmpty(clientId) ? clientId : UUID.randomUUID().toString());
		client.connect(options);
		return client;
	}
}
