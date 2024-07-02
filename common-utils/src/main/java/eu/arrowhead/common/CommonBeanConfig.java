package eu.arrowhead.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.arrowhead.common.http.filter.ArrowheadFilter;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.filter.authentication.CertificateFilter;
import eu.arrowhead.common.http.filter.authentication.NoneFilter;
import eu.arrowhead.common.http.filter.authentication.OutsourcedFilter;


@Configuration
public class CommonBeanConfig {

	//=================================================================================================
	// methods
	
    //-------------------------------------------------------------------------------------------------
    @Bean(Constants.ARROWHEAD_CONTEXT)
    Map<String, Object> getArrowheadContext() {
        return new ConcurrentHashMap<>();
    }
    
    //-------------------------------------------------------------------------------------------------
    @Bean
	ArrowheadFilter authenticationPolicyFilter(@Value(Constants.$AUTHENTICATION_POLICY_WD) final AuthenticationPolicy policy ) {
		switch (policy) {
		case CERTIFICATE: return new CertificateFilter();
		case OUTSOURCED: return new OutsourcedFilter();
		case NONE: return new NoneFilter();
		default:
			throw new IllegalArgumentException("Unknown policy: " + policy.name());
		}
	}
}
