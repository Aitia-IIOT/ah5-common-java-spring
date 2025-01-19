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
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.exception.ForbiddenException;
import eu.arrowhead.common.http.ArrowheadHttpService;
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
	
	@Value(Constants.$FORCE_BLACKLIST_FILTER_WD)
	private boolean force;

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {	
		log.debug("BlacklistFilter is active");
		
		// if requester is sysop, no need for check
		final boolean isSysop = Boolean.valueOf(request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_SYSOP_REQUEST).toString());
		
		// if request is for lookup for authentication, no need for check
		final boolean isAuthLookup = false; //TODO
		
		if (!isSysop && !isAuthLookup) {
			log.debug("checking Blacklist");
			try {
					
				final String systemName = request.getAttribute(Constants.HTTP_ATTR_ARROWHEAD_AUTHENTICATED_SYSTEM).toString();
				
				// if requester is blacklist, no need for check
				if (!systemName.equals(Constants.SYS_NAME_BLACKLIST)) {
						
					final boolean isBlacklisted = arrowheadHttpService.consumeService(Constants.SERVICE_DEF_BLACKLIST_DISCOVERY, Constants.SERVICE_OP_CHECK, boolean.class, List.of(systemName));
			
					if (isBlacklisted) {
						throw new ForbiddenException(systemName + " system is blacklisted!");
					}
				}
			} catch (final ForbiddenException | AuthException ex) {
				throw ex;
			} catch (final ArrowheadException ex) {
				logger.info("Blacklist server is not available.");
				logger.debug("Blacklist server is not available, force blacklist filter: " + force);
				if (force) {
					throw new ForbiddenException("Blacklist system is not available, the system might be blacklisted.");
				} else {
					chain.doFilter(request, response);
				}
			}
		}
		chain.doFilter(request, response);
	}
}
