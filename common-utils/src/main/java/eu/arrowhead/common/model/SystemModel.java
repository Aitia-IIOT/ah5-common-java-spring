package eu.arrowhead.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

public record SystemModel(
		Map<String, Object> metadata,
		String version,
		List<AddressModel> addresses,
		String deviceName) {

	public SystemModel {
		Assert.isTrue(!Utilities.isEmpty(version), "Version null or blank");
		Assert.isTrue(!Utilities.isEmpty(addresses), "Addresses list is null or empty");
	}

	//=================================================================================================
	// nested class

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private String deviceName;
		private String version;
		private List<AddressModel> addresses = new ArrayList<>();
		private Map<String, Object> metadata = new HashMap<>();

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder deviceName(final String deviceName) {
			this.deviceName = deviceName;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder version(final String version) {
			this.version = version;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder address(final AddressModel address) {
			if (addresses == null) {
				addresses = new ArrayList<>();
			}
			addresses.add(address);
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder addresses(final List<AddressModel> addresses) {
			this.addresses = addresses;
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
		public SystemModel build() {
			return new SystemModel(metadata, version, addresses, deviceName);
		}
	}
}