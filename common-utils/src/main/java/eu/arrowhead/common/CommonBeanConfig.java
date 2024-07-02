package eu.arrowhead.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CommonBeanConfig {

	//=================================================================================================
	// methods
	
    //-------------------------------------------------------------------------------------------------
    @Bean(Constants.ARROWHEAD_CONTEXT)
    Map<String, Object> getArrowheadContext() {
        return new ConcurrentHashMap<>();
    }
}
