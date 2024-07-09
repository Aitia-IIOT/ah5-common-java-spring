package eu.arrowhead.common.jpa;

import java.time.ZonedDateTime;

import org.springframework.boot.logging.LogLevel;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class LogEntity {

	private final int varchar = 100;

	//=================================================================================================
	// members
	@Id
	@Column(length = varchar)
	protected String logId;

	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	protected ZonedDateTime entryDate;

	@Column(nullable = true, length = varchar)
	protected String logger;

	@Column(nullable = true, length = varchar)
	@Enumerated(EnumType.STRING)
	protected LogLevel logLevel;

	@Column(nullable = true)
	protected String message;

	@Column(nullable = true)
	protected String exception;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public LogEntity() {
	}

	//-------------------------------------------------------------------------------------------------
	public LogEntity(final String logId, final ZonedDateTime entryDate, final String logger, final LogLevel logLevel, final String message, final String exception) {
		this.logId = logId;
		this.entryDate = entryDate;
		this.logger = logger;
		this.logLevel = logLevel;
		this.message = message;
		this.exception = exception;
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		final String logLevelName = logLevel == null ? "null" : logLevel.name();
		return "Logs [logId = " + logId + ", entryDate = " + entryDate + ", logger = " + logger + ", logLevel = " + logLevelName + ", message = " + message + "]";
	}

	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public String getLogId() {
		return logId;
	}

	//-------------------------------------------------------------------------------------------------
	public void setLogId(final String logId) {
		this.logId = logId;
	}

	//-------------------------------------------------------------------------------------------------
	public ZonedDateTime getEntryDate() {
		return entryDate;
	}

	//-------------------------------------------------------------------------------------------------
	public void setEntryDate(final ZonedDateTime entryDate) {
		this.entryDate = entryDate;
	}

	//-------------------------------------------------------------------------------------------------
	public String getLogger() {
		return logger;
	}

	//-------------------------------------------------------------------------------------------------
	public void setLogger(final String logger) {
		this.logger = logger;
	}

	//-------------------------------------------------------------------------------------------------
	public LogLevel getLogLevel() {
		return logLevel;
	}

	//-------------------------------------------------------------------------------------------------
	public void setLogLevel(final LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	//-------------------------------------------------------------------------------------------------
	public String getMessage() {
		return message;
	}

	//-------------------------------------------------------------------------------------------------
	public void setMessage(final String message) {
		this.message = message;
	}

	//-------------------------------------------------------------------------------------------------
	public String getException() {
		return exception;
	}

	//-------------------------------------------------------------------------------------------------
	public void setException(final String exception) {
		this.exception = exception;
	}

}