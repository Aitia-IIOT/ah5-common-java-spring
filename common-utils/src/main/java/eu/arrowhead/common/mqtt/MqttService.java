package eu.arrowhead.common.mqtt;

import java.util.UUID;

import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;

@Component
public class MqttService {

	//=================================================================================================
	// members

	@Autowired
	private SSLProperties sslProperties;

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public MqttClient createConnection(final String address, final int port, final String clientId, final String username, final String password) throws MqttException {
		logger.debug("createConnection started");
		Assert.isTrue(!Utilities.isEmpty(address), "address is empty");

		String serverURI = sslProperties.isSslEnabled() ? "ssl://" : "tcp://";
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
		if (sslProperties.isSslEnabled()) {
			options.setSocketFactory(sslSettings());
		}

		final MqttClient client = new MqttClient(serverURI, !Utilities.isEmpty(clientId) ? clientId : UUID.randomUUID().toString());
		client.connect(options);
		return client;
	}

	//=================================================================================================
	// assistant methods

	private SSLSocketFactory sslSettings() {
		// TODO
		return null;
	}
}
