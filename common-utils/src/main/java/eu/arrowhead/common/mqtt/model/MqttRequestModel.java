package eu.arrowhead.common.mqtt.model;

import java.util.HashMap;
import java.util.Map;

import eu.arrowhead.common.mqtt.MqttQoS;
import eu.arrowhead.dto.MqttRequestTemplate;

public class MqttRequestModel {

	//=================================================================================================
	// members

	private final String traceId;
	private final String operation;
	private final String requestTopic;
	private final String responseTopic;
	private final MqttQoS qosRequirement;
	private final Map<String, String> params;
	private final Object payload;

	private String requester;
	private boolean isSysOp = false;
	private final Map<String, String> attributes = new HashMap<>();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public MqttRequestModel(final String baseTopic, final String operation, final MqttRequestTemplate template) {
		this.traceId = template.traceId();
		this.requestTopic = baseTopic;
		this.operation = operation;
		this.responseTopic = template.responseTopic();
		this.qosRequirement = MqttQoS.valueOf(template.qosRequirement());
		this.params = template.params() == null ? new HashMap<>() : template.params();
		this.payload = template.payload();
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public String getTraceId() {
		return traceId;
	}

	//-------------------------------------------------------------------------------------------------
	public String getOperation() {
		return operation;
	}

	//-------------------------------------------------------------------------------------------------
	public String getRequestTopic() {
		return requestTopic;
	}

	//-------------------------------------------------------------------------------------------------
	public String getResponseTopic() {
		return responseTopic;
	}

	//-------------------------------------------------------------------------------------------------
	public MqttQoS getQosRequirement() {
		return qosRequirement;
	}

	//-------------------------------------------------------------------------------------------------
	public Map<String, String> getParams() {
		return params;
	}

	//-------------------------------------------------------------------------------------------------
	public Object getPayload() {
		return payload;
	}

	//-------------------------------------------------------------------------------------------------
	public String getRequester() {
		return requester;
	}

	//-------------------------------------------------------------------------------------------------
	public void setRequester(final String requester) {
		this.requester = requester;
	}

	//-------------------------------------------------------------------------------------------------
	public boolean isSysOp() {
		return isSysOp;
	}

	//-------------------------------------------------------------------------------------------------
	public void setSysOp(final boolean isSysOp) {
		this.isSysOp = isSysOp;
	}

	//-------------------------------------------------------------------------------------------------
	public void setAttribute(final String key, final String value) {
		this.attributes.put(key, value);
	}

	//-------------------------------------------------------------------------------------------------
	public String getAttribute(final String key) {
		return this.attributes.get(key);
	}
}