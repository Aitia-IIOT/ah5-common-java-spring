package eu.arrowhead.common.service.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.jpa.LogEntity;
import eu.arrowhead.dto.LogRequestDTO;

@Service
public class LogValidation {

	//=================================================================================================
	// members

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private PageValidator pageValidator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public void validateLogRequest(final LogRequestDTO dto, final String origin) {
		logger.debug("validateLogRequest started...");

		if (dto != null) {
			pageValidator.validatePageParameter(dto.pagination(), LogEntity.SORTABLE_FIELDS_BY, origin);

			// TODO: continue
		}
	}

}
