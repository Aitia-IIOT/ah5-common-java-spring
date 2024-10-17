package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.arrowhead.dto.enums.ExceptionType;

@JsonInclude(Include.NON_NULL)
public record ErrorMessageDTO(
		String errorMessage,
		int errorCode,
		ExceptionType exceptionType,
		String origin) implements ErrorWrapperDTO {

	//-------------------------------------------------------------------------------------------------
	@JsonIgnore
	@Override
	public boolean error() {
		return true;
	}
}
