package eu.arrowhead.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import eu.arrowhead.common.Constants;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	//=================================================================================================
	// members

	@Value(Constants.$CORS_ORIGIN_PATTERNS_WD)
	private String[] originPatterns;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public void addCorsMappings(final CorsRegistry registry) {
		registry.addMapping("/**")
				.allowCredentials(Constants.CORS_ALLOW_CREDENTIALS)
				.maxAge(Constants.CORS_MAX_AGE)
				.allowedHeaders(HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION)
				.allowedOriginPatterns(originPatterns);
	}
}