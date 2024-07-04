package eu.arrowhead.common.http.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;

public record HttpInterfaceModel(
		List<String> accessAddresses,
		int accessPort,
		String basePath,
		Map<String, HttpOperationModel> operations) {

	public HttpInterfaceModel {
		Assert.isTrue(!Utilities.isEmpty(accessAddresses), "'accessAddresses' is missing or empty.");
		Assert.isTrue(accessPort >= Constants.MIN_PORT && accessPort <= Constants.MAX_PORT, "'accessPort' is invalid.");
		Assert.isTrue(!Utilities.isEmpty(basePath), "'basePath' is missing.");
		Assert.isTrue(!Utilities.isEmpty(operations), "'operations' is missing or empty.");
	}

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private List<String> accessAddresses = new ArrayList<>();
		private int accessPort;
		private String basePath;
		private Map<String, HttpOperationModel> operations = new HashMap<>();

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder() {
		}

		//-------------------------------------------------------------------------------------------------
		public Builder(final String domainName, final int port) {
			accessAddresses.add(domainName);
			accessPort = port;
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
		public Builder operations(final Map<String, HttpOperationModel> operations) {
			this.operations = operations;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder operation(final String operationName, final HttpOperationModel model) {
			if (operations == null) {
				operations = new HashMap<>();
			}
			operations.put(operationName, model);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public HttpInterfaceModel build() {
			return new HttpInterfaceModel(accessAddresses, accessPort, basePath, operations);
		}

	}
}
