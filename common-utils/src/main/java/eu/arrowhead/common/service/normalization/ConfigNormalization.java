package eu.arrowhead.common.service.normalization;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.Utilities;

@Service
public class ConfigNormalization {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public List<String> normalizeConfigKeyList(final List<String> keys) {
		logger.debug("normalizeConfigKeyList started");

		if (Utilities.isEmpty(keys)) {
			return List.of();
		}

		return keys
				.stream()
				.map(k -> k.trim())
				.distinct()
				.collect(Collectors.toList());
	}
}