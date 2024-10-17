package eu.arrowhead.common.mqtt;

import java.util.HashMap;
import java.util.Map;
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

	private Map<String, MqttClient> clientMap = new HashMap<>();

	private final Logger logger = LogManager.getLogger(getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public MqttClient client(final String connectId) {
		Assert.isTrue(!Utilities.isEmpty(connectId), "connectId is empty");
		return clientMap.get(connectId);
	}

	//-------------------------------------------------------------------------------------------------
	public void connect(final String connectId, final String address, final int port, final String clientId, final String username, final String password) throws MqttException {
		logger.debug("createConnection started");
		Assert.isTrue(!Utilities.isEmpty(connectId), "connectId is empty");
		Assert.isTrue(!Utilities.isEmpty(address), "address is empty");

		if (clientMap.containsKey(connectId)) {
			disconnect(connectId);
		}

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
		clientMap.put(connectId, client);

		logger.info("Connected to MQTT broker: " + client.getServerURI());
	}

	//-------------------------------------------------------------------------------------------------
	public void disconnect(final String connectId) throws MqttException {
		logger.debug("disconnect started");

		final MqttClient client = clientMap.get(connectId);
		if (client != null) {
			client.disconnect();
			clientMap.remove(connectId);
			logger.info("Disconnected from MQTT broker");
		}
	}

	//=================================================================================================
	// assistant methods

	private SSLSocketFactory sslSettings() {
		// TODO
		return null;
	}
}
