package eu.arrowhead.common;

public class Constants {

	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "eu.arrowhead";
	
	public static final String HTTP_STATUS_OK = "200";
	public static final String HTTP_STATUS_BAD_REQUEST = "400";
	public static final String HTTP_STATUS_UNAUTHORIZED = "401";
	public static final String HTTP_STATUS_FORBIDDEN = "403";
	public static final String HTTP_STATUS_NOT_FOUND = "404";
	public static final String HTTP_STATUS_INTERNAL_SERVER_ERROR = "500";

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
