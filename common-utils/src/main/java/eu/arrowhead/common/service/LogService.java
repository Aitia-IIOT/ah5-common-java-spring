package eu.arrowhead.common.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.service.validation.LogValidation;
import eu.arrowhead.dto.LogEntryListResponseDTO;
import eu.arrowhead.dto.LogRequestDTO;

@Service
public class LogService {

	//=================================================================================================
	// members
	
	@Autowired
	private LogValidation validator;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LogEntryListResponseDTO getLogEntries(final LogRequestDTO dto, final String origin) {
		logger.debug("getLogEntries started...");

		validator.validateLogRequest(dto, origin);
		
		return null;
	}

}
