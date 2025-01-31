package eu.arrowhead.common.jpa;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.boot.logging.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LogEntityRepository<T extends LogEntity> extends RefreshableRepository<T, String> {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Page<LogEntity> findAllByLogLevelInAndEntryDateBetween(final List<LogLevel> levels, final ZonedDateTime from, final ZonedDateTime to, final Pageable pageable);

	//-------------------------------------------------------------------------------------------------
	public Page<LogEntity> findAllByLogLevelInAndEntryDateBetweenAndLoggerContainsIgnoreCase(
			final List<LogLevel> levels,
			final ZonedDateTime from,
			final ZonedDateTime to,
			final String logger,
			final Pageable pageable);
}