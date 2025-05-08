package eu.arrowhead.common.service.validation.name;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.service.normalization.NormalizationMode;

@Component
public class SystemNameNormalizer {

	//=================================================================================================
	// members
	
	@Value(Constants.$NORMALIZATION_MODE_WD)
	private NormalizationMode normalizationMode;

	private final Logger logger = LogManager.getLogger(this.getClass());

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public String normalize(final String name) {
		// TODO: implement
		
		return name;
	}
}