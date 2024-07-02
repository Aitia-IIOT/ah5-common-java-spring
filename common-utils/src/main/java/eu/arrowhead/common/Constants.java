package eu.arrowhead.common;

public class Constants {

	
	//=================================================================================================
	// members
	
	// Global
	
	public static final String LOCALHOST = "localhost";
	public static final String PKCS12 = "PKCS12";
	public static final String APPLICATION_PROPERTIES = "application.properties";
	public static final String BASE_PACKAGE = "eu.arrowhead";
	public static final String HTTPS = "https";
	public static final String HTTP = "http";
	public static final String UNKNOWN = "<unknown>";
	public static final String ARROWHEAD_CONTEXT = "arrowheadContext";
	
	// System related
	
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
	public static final String DOMAIN_PORT = "domain.port";
	public static final String $DOMAIN_PORT = "${" + DOMAIN_PORT + ":0}";
	public static final String SERVICEREGISTRY_ADDRESS = "service.registry.address";
	public static final String $SERVICEREGISTRY_ADDRESS_WD = "${" + SERVICEREGISTRY_ADDRESS + ":" + LOCALHOST + "}";
	public static final String SERVICEREGISTRY_PORT = "service.registry.port";
	public static final String $SERVICEREGISTRY_PORT_WD = "${" + SERVICEREGISTRY_PORT + ":8843}";

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
	public static final String TRUSTSTORE_PATH = "server.ssl.trust-store";
	public static final String $TRUSTSTORE_PATH_WD = "${" + TRUSTSTORE_PATH + ":}";
	public static final String TRUSTSTORE_PASSWORD = "server.ssl.trust-store-password"; 
	public static final String $TRUSTSTORE_PASSWORD_WD = "${" + TRUSTSTORE_PASSWORD + ":}"; 
	public static final String DISABLE_HOSTNAME_VERIFIER = "disable.hostname.verifier";
	public static final String $DISABLE_HOSTNAME_VERIFIER_WD = "${" + DISABLE_HOSTNAME_VERIFIER + ":false}";
	
	//  HTTP related
	
	public static final String HTTP_STATUS_OK = "200";
	public static final String HTTP_STATUS_BAD_REQUEST = "400";
	public static final String HTTP_STATUS_UNAUTHORIZED = "401";
	public static final String HTTP_STATUS_FORBIDDEN = "403";
	public static final String HTTP_STATUS_NOT_FOUND = "404";
	public static final String HTTP_STATUS_INTERNAL_SERVER_ERROR = "500";
	
	public static final String HTTP_CLIENT_CONNECTION_TIMEOUT = "http.client.connection.timeout";
	public static final String $HTTP_CLIENT_CONNECTION_TIMEOUT_WD = "${" + HTTP_CLIENT_CONNECTION_TIMEOUT + ":30000}";
	public static final String HTTP_CLIENT_SOCKET_TIMEOUT = "http.client.socket.timeout";
	public static final String $HTTP_CLIENT_SOCKET_TIMEOUT_WD = "${" + HTTP_CLIENT_SOCKET_TIMEOUT + ":30000}";
	public static final String LOG_ALL_REQUEST_AND_RESPONSE = "log.all.request.and.response";
	public static final String $LOG_ALL_REQUEST_AND_RESPONSE_WD = "${" + LOG_ALL_REQUEST_AND_RESPONSE + ":false}";

	// Swagger
	
	public static final String SWAGGER_UI_URI = "/swagger-ui/index.html";
	public static final String SWAGGER_HTTP_200_MESSAGE = "Service is available";
	public static final String SWAGGER_HTTP_201_MESSAGE = "Created";
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
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private Constants() {
		throw new UnsupportedOperationException();
	}

}
