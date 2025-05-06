package eu.arrowhead.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record OrchestrationNotifyInterfaceDTO(String protocol, Map<String, String> properties) {

}
