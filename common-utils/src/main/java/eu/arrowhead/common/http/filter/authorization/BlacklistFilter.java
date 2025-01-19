package eu.arrowhead.common.http.filter.authorization;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.SystemInfo;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.http.ArrowheadHttpService;
import eu.arrowhead.common.http.HttpUtilities;
import eu.arrowhead.common.http.filter.ArrowheadFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@ConditionalOnProperty(name = Constants.ENABLE_BLACKLIST_FILTER, matchIfMissing = true)
@Order(Constants.REQUEST_FILTER_ORDER_AUTHORIZATION_BLACKLIST)
public class BlacklistFilter extends ArrowheadFilter {
	
	//=================================================================================================
	// members
	
	@Autowired
	protected SystemInfo sysInfo;
	
	@Autowired
	protected ArrowheadHttpService arrowheadHttpService;

	//=================================================================================================
	// assistant methods

	// TODO this filter should lookup for the blacklist service and use it against to the system name
	// if system name is found on the blacklist throw ForbiddenException
	// if requester is sysop, no need for check
	// if request is for lookup for authentication, no need for check
	// blacklist entry can be temporary (with expiration)
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {	
		log.debug("BlacklistFilter is active");
		
		// if requester is sysop, no need for blacklist check
		final Boolean isSysop = Boolean.valueOf(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST).toString());
		//TODO: check if BL system unreachable (app.props) force.blacklist.filter
		if (!isSysop && !sysInfo.getSystemName().equals(Constants.SYS_NAME_BLACKLIST)) {
			log.debug("checking Blacklist");
			try {
					
				final String systemName = request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString();
				
				if (!systemName.equals(Constants.SYS_NAME_BLACKLIST)) {
						
					final boolean isBlacklisted = arrowheadHttpService.consumeService(Constants.SERVICE_DEF_BLACKLIST_DISCOVERY, Constants.SERVICE_OP_CHECK, boolean.class, List.of(systemName));
			
					if (isBlacklisted) {
						throw new ForbiddenException(systemName + " system is blacklisted!");
					}
				}
				
			} catch (final ArrowheadException ex) {
				handleException(ex, response);
			}
		}
		chain.doFilter(request, response);
	}
}
