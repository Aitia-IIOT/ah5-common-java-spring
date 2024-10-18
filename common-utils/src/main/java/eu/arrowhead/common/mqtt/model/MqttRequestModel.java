package eu.arrowhead.common.mqtt.model;

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
	private final Object payload;

	private String requester;
	private boolean isSysOp = false;

	//=================================================================================================
	// methods

	public MqttRequestModel(final String requestTopic, final MqttRequestTemplate template) {
		this.traceId = template.traceId();
		this.operation = template.operation();
		this.requestTopic = requestTopic;
		this.responseTopic = template.responseTopic();
		this.qosRequirement = MqttQoS.valueOf(template.qosRequirement());
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

}
