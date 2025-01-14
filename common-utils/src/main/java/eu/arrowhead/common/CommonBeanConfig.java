package eu.arrowhead.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.arrowhead.common.collector.HttpCollectorDriver;
import eu.arrowhead.common.collector.ICollectorDriver;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import eu.arrowhead.common.http.filter.authentication.AuthenticationPolicy;
import eu.arrowhead.common.http.filter.authentication.CertificateFilter;
import eu.arrowhead.common.http.filter.authentication.OutsourcedFilter;
import eu.arrowhead.common.http.filter.authentication.SelfDeclaredFilter;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.filter.authentication.CertificateMqttFilter;
import eu.arrowhead.common.mqtt.filter.authentication.OutsourcedMqttFilter;
import eu.arrowhead.common.mqtt.filter.authentication.SelfDeclaredMqttFilter;

@Configuration
public class CommonBeanConfig {

	//=================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	@Bean(Constants.ARROWHEAD_CONTEXT)
	Map<String, Object> getArrowheadContext() {
		return new ConcurrentHashMap<>();
	}

	//-------------------------------------------------------------------------------------------------
	@Bean
	ArrowheadFilter authenticationPolicyFilter(@Value(Constants.$AUTHENTICATION_POLICY_WD) final AuthenticationPolicy policy) {
		switch (policy) {
		case CERTIFICATE:
			return new CertificateFilter();
		case OUTSOURCED:
			return new OutsourcedFilter();
		case DECLARED:
			return new SelfDeclaredFilter();
		default:
			throw new IllegalArgumentException("Unknown policy: " + policy.name());
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Bean
	ArrowheadMqttFilter authenticationPolicyMqttFilter(@Value(Constants.$MQTT_API_ENABLED_WD) final boolean isMqttEnabled, @Value(Constants.$AUTHENTICATION_POLICY_WD) final AuthenticationPolicy policy) {
		if (!isMqttEnabled) {
			return null;
		}

		switch (policy) {
		case CERTIFICATE:
			return new CertificateMqttFilter();
		case OUTSOURCED:
			return new OutsourcedMqttFilter();
		case DECLARED:
			return new SelfDeclaredMqttFilter();
		default:
			throw new IllegalArgumentException("Unknown policy: " + policy.name());
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Bean
	ICollectorDriver getDefaultCollectorDriver() {
		return new HttpCollectorDriver();
	}
}