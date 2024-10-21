package eu.arrowhead.dto;

import java.util.Map;

// TODO secretKey input field in order to encrypt the response (TLS only!)
public record MqttRequestTemplate(
		String traceId,
		String operation,
		String authentication,
		String responseTopic,
		Integer qosRequirement,
		Map<String, String> params,
		Object payload) {
}
