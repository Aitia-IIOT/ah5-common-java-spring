package eu.arrowhead.common.service.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.common.jpa.LogEntity;
import eu.arrowhead.dto.LogRequestDTO;
import eu.arrowhead.dto.PageDTO;

@ExtendWith(MockitoExtension.class)
public class LogValidationTest {

	//=================================================================================================
	// members

	@InjectMocks
	private final LogValidation validator = new LogValidation();

	@Mock
	private PageValidator pageValidator;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateLogRequestInvalidFrom() {
		doNothing().when(pageValidator).validatePageParameter(any(PageDTO.class), anyList(), eq("test"));

		final PageDTO pageDto = new PageDTO(0, 10, "ASC", "logId");
		final LogRequestDTO request = new LogRequestDTO(
				pageDto,
				"invalid-timestamp",
				null,
				null,
				null);

		final Throwable ex = assertThrows(
				InvalidParameterException.class,
				() -> validator.validateLogRequest(request, "test"));

		verify(pageValidator).validatePageParameter(pageDto, LogEntity.SORTABLE_FIELDS_BY, "test");

		assertEquals("Invalid date time in field: from. Please use a UTC date time in ISO-8601 format", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateLogRequestInvalidTo() {
		doNothing().when(pageValidator).validatePageParameter(any(PageDTO.class), anyList(), eq("test"));

		final PageDTO pageDto = new PageDTO(0, 10, "ASC", "logId");
		final LogRequestDTO request = new LogRequestDTO(
				pageDto,
				"2025-06-26T12:00:00Z",
				"invalid-timestamp",
				null,
				null);

		final Throwable ex = assertThrows(
				InvalidParameterException.class,
				() -> validator.validateLogRequest(request, "test"));

		verify(pageValidator).validatePageParameter(pageDto, LogEntity.SORTABLE_FIELDS_BY, "test");

		assertEquals("Invalid date time in field: to. Please use a UTC date time in ISO-8601 format", ex.getMessage());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testValidateLogRequestOk() {
		assertAll("valid log request",
				() -> assertDoesNotThrow(() -> validator.validateLogRequest(null, "test")));
	}
}
