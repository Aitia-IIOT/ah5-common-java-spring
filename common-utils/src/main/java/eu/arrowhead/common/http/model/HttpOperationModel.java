package eu.arrowhead.common.http.model;

import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

public record HttpOperationModel(
		String path,
		String method) {

	public HttpOperationModel {
		Assert.isTrue(!Utilities.isEmpty(path), "'path' is missing.");
		Assert.isTrue(!Utilities.isEmpty(method), "'method' is missing.");
	}

	//=================================================================================================
	// members
	public static final String PROP_NAME_PATH = "path";
	public static final String PROP_NAME_METHOD = "method";

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private String path;
		private String method;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder path(final String path) {
			this.path = path;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder method(final String method) {
			this.method = method;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public HttpOperationModel build() {
			return new HttpOperationModel(path, method);
		}
	}
}
