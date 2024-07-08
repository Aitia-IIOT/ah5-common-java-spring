package eu.arrowhead.common.http.model;

import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

public record HttpOperationModel(
		String path,
		HttpMethod method) {

	public HttpOperationModel {
		Assert.isTrue(!Utilities.isEmpty(path), "'path' is missing.");
		Assert.notNull(method, "'method' is missing.");
	}

	//=================================================================================================
	// nested classes

	//-------------------------------------------------------------------------------------------------
	public static class Builder {

		//=================================================================================================
		// members

		private String path;
		private HttpMethod method;

		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder path(final String path) {
			this.path = path;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public Builder method(final HttpMethod method) {
			this.method = method;
			return this;
		}

		//-------------------------------------------------------------------------------------------------
		public HttpOperationModel build() {
			return new HttpOperationModel(path, method);
		}
	}
}