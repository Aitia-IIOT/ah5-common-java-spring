package eu.arrowhead.common.http.filter.authorization;

import org.springframework.core.annotation.Order;

import eu.arrowhead.common.http.filter.ArrowheadFilter;

@Order(20)
public class ManagementServiceFilter extends ArrowheadFilter {

	// TODO: if an endpoint is management, then check auth rules or if sysop
	// requester comes from the request attribute "arrowhead.authenticated"
}
