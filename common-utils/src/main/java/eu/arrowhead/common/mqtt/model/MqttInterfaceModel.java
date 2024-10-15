package eu.arrowhead.common.mqtt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.model.InterfaceModel;

public record MqttInterfaceModel(
		String templateName,
		List<String> accessAddresses,
		int accessPort,
		String basePath,
		Map<String, String> operations) implements InterfaceModel {

	public MqttInterfaceModel {
		Assert.isTrue(!Utilities.isEmpty(templateName), "'templateName' is missing or empty.");
		Assert.isTrue(!Utilities.isEmpty(accessAddresses), "'accessAddresses' is missing or empty.");
		Assert.isTrue(accessPort >= Constants.MIN_PORT && accessPort <= Constants.MAX_PORT, "'accessPort' is invalid.");
		Assert.isTrue(!Utilities.isEmpty(operations), "'operations' is missing or empty.");
	}

	//=================================================================================================
	// members

	private static final String mqttTemplateName = "generic-mqtt";
	private static final String PROP_NAME_ACCESS_ADDRESSES = "accessAddresses";
	private static final String PROP_NAME_ACCESS_PORT = "accessPort";
	private static final String PROP_NAME_BASE_PATH = "basePath";
	private static final String PROP_NAME_OPERATIONS = "operations";

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String protocol() {
		return templateName.equals(mqttTemplateName) ? "tcp" : "ssl";
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public Map<String, Object> properties() {
		return Map.of(PROP_NAME_ACCESS_ADDRESSES, accessAddresses,
				      PROP_NAME_ACCESS_PORT, accessPort,
				      PROP_NAME_BASE_PATH, basePath,
				      PROP_NAME_OPERATIONS, operations);
	}

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private final String templateName;
		private List<String> accessAddresses = new ArrayList<>();
		private int accessPort;
		private String basePath;
		private Map<String, String> operations = new HashMap<>();

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder(final String templateName) {
			this.templateName = templateName;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder(final String templateName, final String domainName, final int port) {
			this(templateName);
			this.accessAddresses.add(domainName);
			this.accessPort = port;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder accessAddresses(final List<String> accessAddresses) {
			this.accessAddresses = accessAddresses;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder accessAddress(final String address) {
			if (accessAddresses == null) {
				accessAddresses = new ArrayList<>();
			}
			accessAddresses.add(address);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder accessPort(final int accessPort) {
			this.accessPort = accessPort;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder basePath(final String basePath) {
			this.basePath = basePath;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder operations(final Map<String, String> operations) {
			this.operations = operations;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder operation(final String operationName, final String model) {
			if (operations == null) {
				operations = new HashMap<>();
			}
			operations.put(operationName, model);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public MqttInterfaceModel build() {
			return new MqttInterfaceModel(templateName, accessAddresses, accessPort, basePath, operations);
		}

	}
}
