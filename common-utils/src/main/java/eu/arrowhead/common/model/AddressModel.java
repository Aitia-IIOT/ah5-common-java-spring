package eu.arrowhead.common.model;

import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.dto.enums.AddressType;

public record AddressModel(
		AddressType type,
		String address) {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public AddressModel {
		Assert.notNull(type, "Address type is null.");
		Assert.isTrue(!Utilities.isEmpty(address), "Address is null or blank.");
	}

	//=================================================================================================
	// nested class

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private AddressType type;
		private String address;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder type(final AddressType type) {
			this.type = type;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder address(final String address) {
			this.address = address;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public AddressModel build() {
			return new AddressModel(type, address);
		}
	}
}