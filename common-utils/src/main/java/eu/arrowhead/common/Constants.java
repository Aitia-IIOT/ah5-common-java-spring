package eu.arrowhead.common;

public final class Constants {

	//=================================================================================================
	// members

	// Framework version
	public static final String AH_FRAMEWORK_VERSION = "5.0.0";

	// Global

	public static final String UTC = "UTC";
	public static final String LOCALHOST = "localhost";
	public static final String PKCS12 = "PKCS12";
	public static final String APPLICATION_PROPERTIES = "application.properties";
	public static final String BASE_PACKAGE = "eu.arrowhead";
	public static final String HTTPS = "https";
	public static final String HTTP = "http";
	public static final int HTTP_PORT = 80;
	public static final String UNKNOWN = "<unknown>";
	public static final String ARROWHEAD_CONTEXT = "arrowheadContext";
	public static final String SERVER_STANDALONE_MODE = "server.standalone.mode";
	public static final String SERVER_COMMON_NAME = "server.common.name";
	public static final String SERVER_PUBLIC_KEY = "server.public.key";
	public static final String SERVER_PRIVATE_KEY = "server.private.key";
	public static final String SERVER_CERTIFICATE = "server.certificate";

	public static final String KEY_IDENTITY_TOKEN = "identity-token";
	public static final String KEY_PREFIX_FOR_SERVICE_MODEL = "service-model$$";

	public static final String GENERIC_HTTP_INTERFACE_TEMPLATE_NAME = "GENERIC_HTTP";
	public static final String GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME = "GENERIC_HTTPS";

	public static final int MIN_PORT = 1;
	public static final int MAX_PORT = 65535;
	public static final long CONVERSION_MILLISECOND_TO_SECOND = 1000;
	public static final long CONVERSION_MILLISECOND_TO_MINUTE = 60000;

	// System related

	public static final String SYS_NAME_SERVICE_REGISTRY = "serviceregistry";

	public static final String SECURITY_REQ_AUTHORIZATION = "Authorization";

	public static final String DATABASE_URL = "spring.datasource.url";
	public static final String DATABASE_USER = "spring.datasource.username";
	public static final String DATABASE_PASSWORD = "spring.datasource.password";
	public static final String DATABASE_DRIVER_CLASS = "spring.datasource.driver-class-name";
	public static final String SERVER_ADDRESS = "server.address";
	public static final String $SERVER_ADDRESS = "${" + SERVER_ADDRESS + ":}";
	public static final String SERVER_PORT = "server.port";
	public static final String $SERVER_PORT = "${" + SERVER_PORT + ":0}";
	public static final String DOMAIN_NAME = "domain.name";
	public static final String $DOMAIN_NAME = "${" + DOMAIN_NAME + ":}";
	public static final String SERVICEREGISTRY_ADDRESS = "service.registry.address";
	public static final String $SERVICEREGISTRY_ADDRESS_WD = "${" + SERVICEREGISTRY_ADDRESS + ":" + LOCALHOST + "}";
	public static final String SERVICEREGISTRY_PORT = "service.registry.port";
	public static final String $SERVICEREGISTRY_PORT_WD = "${" + SERVICEREGISTRY_PORT + ":8843}";
	public static final String AUTHENTICATION_POLICY = "authentication.policy";
	public static final String $AUTHENTICATION_POLICY_WD = "${" + AUTHENTICATION_POLICY + ":CERTIFICATE}";
	public static final String ENABLE_MANAGEMENT_FILTER = "enable.management.filter";
	public static final String ALLOW_SELF_ADDRESSING = "allow.self.addressing";
	public static final String $ALLOW_SELF_ADDRESSING_WD = "${" + ALLOW_SELF_ADDRESSING + ":true}";
	public static final String ALLOW_NON_ROUTABLE_ADDRESSING = "allow.non.routable.addressing";
	public static final String $ALLOW_NON_ROUTABLE_ADDRESSING_WD = "${" + ALLOW_NON_ROUTABLE_ADDRESSING + ":true}";

	public static final String METADATA_KEY_X509_PUBLIC_KEY = "x509-public-key";

	// SSL related

	public static final String SERVER_SSL_ENABLED = "server.ssl.enabled";
	public static final String $SERVER_SSL_ENABLED_WD = "${" + SERVER_SSL_ENABLED + ":false}";
	public static final String KEYSTORE_TYPE = "server.ssl.key-store-type";
	public static final String $KEYSTORE_TYPE_WD = "${" + KEYSTORE_TYPE + ":" + PKCS12 + "}";
	public static final String KEYSTORE_PATH = "server.ssl.key-store";
	public static final String $KEYSTORE_PATH_WD = "${" + KEYSTORE_PATH + ":}";
	public static final String KEYSTORE_PASSWORD = "server.ssl.key-store-password";
	public static final String $KEYSTORE_PASSWORD_WD = "${" + KEYSTORE_PASSWORD + ":}";
	public static final String KEY_PASSWORD = "server.ssl.key-password";
	public static final String $KEY_PASSWORD_WD = "${" + KEY_PASSWORD + ":}";
	public static final String KEY_ALIAS = "server.ssl.key-alias";
	public static final String $KEY_ALIAS_WD = "${" + KEY_ALIAS + ":}";
	public static final String TRUSTSTORE_PATH = "server.ssl.trust-store";
	public static final String $TRUSTSTORE_PATH_WD = "${" + TRUSTSTORE_PATH + ":}";
	public static final String TRUSTSTORE_PASSWORD = "server.ssl.trust-store-password";
	public static final String $TRUSTSTORE_PASSWORD_WD = "${" + TRUSTSTORE_PASSWORD + ":}";
	public static final String DISABLE_HOSTNAME_VERIFIER = "disable.hostname.verifier";
	public static final String $DISABLE_HOSTNAME_VERIFIER_WD = "${" + DISABLE_HOSTNAME_VERIFIER + ":false}";

	// HTTP related

	public static final String HTTP_STATUS_OK = "200";
	public static final String HTTP_STATUS_CREATED = "201";
	public static final String HTTP_STATUS_NO_CONTENT = "204";
	public static final String HTTP_STATUS_BAD_REQUEST = "400";
	public static final String HTTP_STATUS_UNAUTHORIZED = "401";
	public static final String HTTP_STATUS_FORBIDDEN = "403";
	public static final String HTTP_STATUS_NOT_FOUND = "404";
	public static final String HTTP_STATUS_INTERNAL_SERVER_ERROR = "500";

	public static final String HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM = "arrowhead.authenticated.system";
	public static final String HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST = "arrowhead.sysop.request";
	public static final String HTTP_ATTR_JAKARTA_SERVLET_REQUEST_X509_CERTIFICATE = "jakarta.servlet.request.X509Certificate";
	public static final String HTTP_HEADER_AUTHORIZATION_DELIMITER = "//";
	public static final String HTTP_HEADER_AUTHORIZATION_SCHEMA = "Bearer";
	public static final String HTTP_HEADER_AUTHORIZATION_PREFIX_SYSTEM = "SYSTEM";
	public static final String HTTP_HEADER_AUTHORIZATION_PREFIX_IDENTITY_TOKEN = "IDENTITY-TOKEN";
	public static final String HTTP_HEADER_AUTHORIZATION_PREFIX_AUTH_TOKEN = "AUTH-TOKEN";

	public static final String HTTP_CLIENT_CONNECTION_TIMEOUT = "http.client.connection.timeout";
	public static final String $HTTP_CLIENT_CONNECTION_TIMEOUT_WD = "${" + HTTP_CLIENT_CONNECTION_TIMEOUT + ":30000}";
	public static final String HTTP_CLIENT_SOCKET_TIMEOUT = "http.client.socket.timeout";
	public static final String $HTTP_CLIENT_SOCKET_TIMEOUT_WD = "${" + HTTP_CLIENT_SOCKET_TIMEOUT + ":30000}";
	public static final String LOG_ALL_REQUEST_AND_RESPONSE = "log.all.request.and.response";
	public static final String $LOG_ALL_REQUEST_AND_RESPONSE_WD = "${" + LOG_ALL_REQUEST_AND_RESPONSE + ":false}";

	// Swagger

	public static final String SWAGGER_API_DOCS_URI = "/v3/api-docs";
	public static final String SWAGGER_UI_URI = "/swagger-ui";
	public static final String SWAGGER_UI_INDEX_HTML = SWAGGER_UI_URI + "/index.html";
	public static final String SWAGGER_HTTP_200_MESSAGE = "Ok";
	public static final String SWAGGER_HTTP_201_MESSAGE = "Created";
	public static final String SWAGGER_HTTP_204_MESSAGE = "No changes was necessary";
	public static final String SWAGGER_HTTP_400_MESSAGE = "Bad request";
	public static final String SWAGGER_HTTP_401_MESSAGE = "You are not authenticated";
	public static final String SWAGGER_HTTP_403_MESSAGE = "You have no permission";
	public static final String SWAGGER_HTTP_404_MESSAGE = "Not found";
	public static final String SWAGGER_HTTP_500_MESSAGE = "Internal server error";

	// CORS defaults

	public static final long CORS_MAX_AGE = 600;
	public static final boolean CORS_ALLOW_CREDENTIALS = true;
	public static final String DEFAULT_CORS_ORIGIN_PATTERN = "*";
	public static final String CORS_ORIGIN_PATTERNS = "cors.origin.patterns";
	public static final String $CORS_ORIGIN_PATTERNS_WD = "${" + CORS_ORIGIN_PATTERNS + ":*}";

	// Service related

	public static final String SERVICE_DEF_SYSTEM_DISCOVERY = "system-discovery";
	public static final String SERVICE_DEF_SERVICE_DISCOVERY = "service-discovery";

	// Operation related

	public static final String SERVICE_OP_REGISTER = "register";
	public static final String SERVICE_OP_REVOKE = "revoke";

	public static final String HTTP_API_OP_LOGS_PATH = "/logs";

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Constants() {
		throw new UnsupportedOperationException();
	}

}
