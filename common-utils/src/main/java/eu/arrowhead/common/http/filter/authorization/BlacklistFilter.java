package eu.arrowhead.common.http.filter.authorization;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.collector.ServiceCollector;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.common.http.HttpUtilities;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import eu.arrowhead.common.http.filter.thirdparty.MultiReadRequestWrapper;
import eu.arrowhead.common.http.model.HttpInterfaceModel;
import eu.arrowhead.common.http.model.HttpOperationModel;
import eu.arrowhead.common.model.InterfaceModel;
import eu.arrowhead.common.model.ServiceModel;
import eu.arrowhead.dto.ServiceInstanceLookupRequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ConditionalOnProperty(name = Constants.ENABLE_BLACKLIST_FILTER, matchIfMissing = false)
@Order(Constants.REQUEST_FILTER_ORDER_AUTHORIZATION_BLACKLIST)
public class BlacklistFilter extends ArrowheadFilter {

	//=================================================================================================
	// members

	@Autowired
	protected SystemInfo sysInfo;

	@Autowired
	protected ArrowheadHttpService arrowheadHttpService;

	@Autowired
	private ServiceCollector collector;

	@Value(Constants.$FORCE_BLACKLIST_FILTER_WD)
	private boolean force;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		log.debug("BlacklistFilter is active");

		final MultiReadRequestWrapper requestWrapper = (request instanceof MultiReadRequestWrapper) ? (MultiReadRequestWrapper) request :  new MultiReadRequestWrapper(request);

		// if requester is sysop, no need for check
		final boolean isSysop = HttpUtilities.isSysop(requestWrapper, "BlacklistFilter.doFilterInternal");

		// if request is for lookup for authentication, no need for check
		final boolean isAuthLookup = isAuthenticationLookup(requestWrapper);

		if (!isSysop && !isAuthLookup) {
			log.debug("checking Blacklist");
			try {

				final String systemName = request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString();

				// if requester is blacklist, no need for check
				if (!systemName.equals(Constants.SYS_NAME_BLACKLIST)) {

					final boolean isBlacklisted = arrowheadHttpService.consumeService(Constants.SERVICE_DEF_BLACKLIST_DISCOVERY, Constants.SERVICE_OP_CHECK, Boolean.TYPE, List.of(systemName));

					if (isBlacklisted) {
						throw new ForbiddenException(systemName + " system is blacklisted!");
					}
				}
			} catch (final ForbiddenException | AuthException ex) {
				throw ex;
			} catch (final ArrowheadException ex) {
				logger.error("Blacklist server is not available.");
				logger.debug("Blacklist server is not available, force blacklist filter: " + force);
				if (force) {
					throw new ForbiddenException("Blacklist system is not available, the system might be blacklisted.");
				}
			}
		}
		chain.doFilter(requestWrapper, response);
	}

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	private boolean isAuthenticationLookup(final MultiReadRequestWrapper request) {

		// is the filter running inside SR?

		if (!sysInfo.getSystemName().equals(Constants.SYS_NAME_SERVICE_REGISTRY)) {
			return false;
		}

		// is the request lookup?

		final String requestTarget = Utilities.stripEndSlash(request.getRequestURL().toString());
		// finding the path and method of the lookup operation
		HttpOperationModel lookupOp = null;
		final ServiceModel model = collector.getServiceModel(Constants.SERVICE_DEF_SERVICE_DISCOVERY, sysInfo.isSslEnabled() ? Constants.GENERIC_HTTPS_INTERFACE_TEMPLATE_NAME : Constants.GENERIC_HTTP_INTERFACE_TEMPLATE_NAME);
		for (final InterfaceModel intf : model.interfaces()) {
			final Map<String, HttpOperationModel> ops = (Map<String, HttpOperationModel>) intf.properties().get(HttpInterfaceModel.PROP_NAME_OPERATIONS);
			if (ops.containsKey(Constants.SERVICE_OP_LOOKUP)) {
				// if there is a lookup operation, we can get its path and method
				lookupOp = ops.get(Constants.SERVICE_OP_LOOKUP);
			}
		}
		if (lookupOp == null || !requestTarget.endsWith(lookupOp.path()) || !request.getMethod().equalsIgnoreCase(lookupOp.method())) {
		// SR does not provide lookup operation or the request is not lookup
			return false;
		}

		// is the requester looking for the identity service definition?

		ServiceInstanceLookupRequestDTO dto = null; //expected type for service definition lookup
		try {
			// check if the content type can be mapped to the expected dto
			dto = Utilities.fromJson(request.getCachedBody(), ServiceInstanceLookupRequestDTO.class);
		} catch (Exception ex) {
			return false;
		}
		if (dto == null || dto.serviceDefinitionNames().size() != 1 || !dto.serviceDefinitionNames().getFirst().equals(Constants.SERVICE_DEF_IDENTITY)) {
			// dto is null or the requester is not (only) looking for the identity
			return false;
		}

		return true;
	}
}
