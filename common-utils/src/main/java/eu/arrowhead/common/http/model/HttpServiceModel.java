package eu.arrowhead.common.http.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

public record HttpServiceModel(
		String serviceDefinition,
		String version,
		List<HttpInterfaceModel> interfaces,
		Map<String, Object> metadata) {

	public HttpServiceModel {
		Assert.isTrue(!Utilities.isEmpty(serviceDefinition), "Service definition is null or blank");
		Assert.isTrue(!Utilities.isEmpty(version), "Version null or blank");
		Assert.isTrue(!Utilities.isEmpty(interfaces), "Interfaces list is null or empty");
	}

	//=================================================================================================
	// nested class

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private String serviceDefinition;
		private String version;
		private List<HttpInterfaceModel> interfaces = new ArrayList<>();
		private Map<String, Object> metadata = new HashMap<>();

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder serviceDefinition(final String serviceDefinition) {
			this.serviceDefinition = serviceDefinition;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder version(final String version) {
			this.version = version;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder serviceInterface(final HttpInterfaceModel serviceInterface) {
			if (interfaces == null) {
				interfaces = new ArrayList<>();
			}
			interfaces.add(serviceInterface);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder serviceInterfaces(final List<HttpInterfaceModel> interfaces) {
			this.interfaces = interfaces;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder metadata(final String key, final Object value) {
			if (metadata == null) {
				metadata = new HashMap<>();
			}
			metadata.put(key, value);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder metadata(final Map<String, Object> metadata) {
			this.metadata = metadata;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public HttpServiceModel build() {
			return new HttpServiceModel(serviceDefinition, version, interfaces, metadata);
		}
	}
}