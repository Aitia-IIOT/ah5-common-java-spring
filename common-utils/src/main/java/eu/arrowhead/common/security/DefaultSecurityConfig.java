package eu.arrowhead.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.http.filter.InboundDebugFilter;
import eu.arrowhead.common.http.filter.OutboundDebugFilter;

@Configuration
public class DefaultSecurityConfig {

	//=================================================================================================
	// members

	//	@Value(SentinelConstants.$SERVER_SSL_ENABLED_WD)
	//	protected boolean sslEnabled;

	@Value(Constants.$LOG_ALL_REQUEST_AND_RESPONSE_WD)
	protected boolean debugMode;

	//-------------------------------------------------------------------------------------------------
	@Bean
	SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		http.httpBasic(basic -> basic.disable())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.NEVER));

		//    	if (sslEnabled) {
		//    		http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
		//    	}
		//
		if (debugMode) {
			http.addFilterBefore(new OutboundDebugFilter(), ChannelProcessingFilter.class);
			http.addFilterAfter(new InboundDebugFilter(), X509AuthenticationFilter.class);
		}

		return http.build();
	}
}