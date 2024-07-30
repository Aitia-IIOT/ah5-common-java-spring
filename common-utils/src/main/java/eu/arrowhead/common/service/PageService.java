package eu.arrowhead.common.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.service.validation.PageValidator;
import eu.arrowhead.dto.PageDTO;

@Service
public class PageService {

	//=================================================================================================
	// members

	private static final Direction DEFAULT_DEFAULT_DIRECTION = Direction.ASC;

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private PageValidator pageValidator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public PageDTO normalizePageParameters(final PageDTO page, final Direction defaultDirection, final List<String> availableSortFields, final String defaultSortField, final String origin) {
		logger.debug("normalizePageParameters started...");

		if (page == null) {
			return new PageDTO(0, Integer.MAX_VALUE, defaultDirection.name(), defaultSortField);
		}

		pageValidator.validatePageParameter(page, availableSortFields, origin);

		final boolean notDefined = page.page() == null && page.size() == null;

		int normalizedPage = notDefined ? -1 : page.page();
		if (normalizedPage < 0) {
			normalizedPage = 0;
		}

		int normalizedSize = notDefined ? -1 : page.size();
		if (normalizedSize < 1) {
			normalizedSize = Integer.MAX_VALUE;
		}

		final Direction normalizedDirection = Utilities.isEmpty(page.direction()) ? defaultDirection : Direction.valueOf(page.direction().trim().toUpperCase());
		final String normalizedSortField = Utilities.isEmpty(page.sortField()) ? defaultSortField : page.sortField().trim();

		return new PageDTO(normalizedPage, normalizedSize, normalizedDirection.name(), normalizedSortField);
	}

	//-------------------------------------------------------------------------------------------------
	public PageDTO normalizePageParameters(final PageDTO page, final List<String> availableSortFields, final String defaultSortField, final String origin) {
		return normalizePageParameters(page, DEFAULT_DEFAULT_DIRECTION, availableSortFields, defaultSortField, origin);
	}

	//-------------------------------------------------------------------------------------------------
	public PageRequest getPageRequest(final PageDTO page, final Direction defaultDirection, final List<String> availableSortFields, final String defaultSortField, final String origin) {
		logger.debug("getPageRequest started...");

		final PageDTO normalized = normalizePageParameters(page, defaultDirection, availableSortFields, defaultSortField, origin);
		return PageRequest.of(normalized.page().intValue(), normalized.size().intValue(), Direction.valueOf(normalized.direction()), normalized.sortField());
	}

	//-------------------------------------------------------------------------------------------------
	public PageRequest getPageRequest(final PageDTO page, final List<String> availableSortFields, final String defaultSortField, final String origin) {
		logger.debug("getPageRequest started...");

		final PageDTO normalized = normalizePageParameters(page, availableSortFields, defaultSortField, origin);
		return PageRequest.of(normalized.page().intValue(), normalized.size().intValue(), Direction.valueOf(normalized.direction()), normalized.sortField());
	}
}