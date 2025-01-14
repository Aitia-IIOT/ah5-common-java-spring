package eu.arrowhead.common.mqtt.filter.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.exception.InternalServerError;
import eu.arrowhead.common.mqtt.filter.ArrowheadMqttFilter;
import eu.arrowhead.common.mqtt.model.MqttRequestModel;

@Service
@ConditionalOnProperty(name = { "mqtt.api.enabled", "enable.management.filter" }, havingValue = "true", matchIfMissing = false)
public class ManagementServiceMqttFilter implements ArrowheadMqttFilter {

	//=================================================================================================
	// members

	@Autowired
	private SystemInfo sysInfo;

	private static final String mgmtPath = "/management";

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	public int order() {
		return Constants.REQUEST_FILTER_ORDER_AUTHORIZATION_MGMT_SERVICE;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void doFilter(final String authKey, final MqttRequestModel request) {
		logger.debug("ManagementServiceMqttFilter.doFilter started...");

		if (request.getRequestTopic().contains(mgmtPath)) {
			final String systemName = request.getRequester();
			boolean allowed = false;

			switch (sysInfo.getManagementPolicy()) {
			case SYSOP_ONLY:
				allowed = request.isSysOp();
				break;

			case WHITELIST:
				allowed = request.isSysOp() || isWhitelisted(systemName);
				break;

			case AUTHORIZATION:
				allowed = request.isSysOp() || isWhitelisted(systemName) || isAuthorized(systemName);
				break;

			default:
				throw new InternalServerError("Unimplemented management policy: " + sysInfo.getManagementPolicy());
			}

			if (!allowed) {
				throw new ForbiddenException("Requester has no management permission");
			}
		}
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	public boolean isWhitelisted(final String systemName) {
		return sysInfo.getManagementWhitelist().contains(systemName);
	}

	//-------------------------------------------------------------------------------------------------
	private boolean isAuthorized(final String systemName) {
		// TODO consume the authorization service
		return false;
	}
}