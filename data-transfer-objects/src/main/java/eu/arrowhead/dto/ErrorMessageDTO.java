package eu.arrowhead.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.arrowhead.dto.enums.ExceptionType;

public record ErrorMessageDTO(String errorMessage,
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
