package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record MqttResponseTemplate(
		int status,
		String traceId,
		String receiver,
		Object payload) {
}