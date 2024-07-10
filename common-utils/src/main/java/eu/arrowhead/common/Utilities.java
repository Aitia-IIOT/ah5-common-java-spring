package eu.arrowhead.common;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Map;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.arrowhead.common.exception.ArrowheadException;

public final class Utilities {

	//=================================================================================================
	// members

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;

	static {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static boolean isEmpty(final String str) {
		return str == null || str.isBlank();
	}

	//-------------------------------------------------------------------------------------------------
	public static boolean isEmpty(final Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	//-------------------------------------------------------------------------------------------------
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String toJson(final Object object) {
		if (object == null) {
			return null;
		}

		try {
			return mapper.writeValueAsString(object);
		} catch (final JsonProcessingException ex) {
			throw new ArrowheadException("The specified object cannot be converted to text.", ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static <T> T fromJson(final String json, final Class<T> parsedClass) {
		if (json == null || parsedClass == null) {
			return null;
		}

		try {
			return mapper.readValue(json, parsedClass);
		} catch (final IOException ex) {
			throw new ArrowheadException("The specified string cannot be converted to a(n) " + parsedClass.getSimpleName() + " object.", ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static <T> T fromJson(final String json, final com.fasterxml.jackson.core.type.TypeReference<T> reference) {
		if (json == null || reference == null) {
			return null;
		}

		try {
			return mapper.readValue(json, reference);
		} catch (final IOException ex) {
			throw new ArrowheadException("The specified string cannot be converted to a(n) " + reference.getType() + " object.", ex);
		}
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String toPrettyJson(final String jsonString) {
		try {
			if (jsonString != null) {
				final String jsonString_ = jsonString.trim();
				if (jsonString_.startsWith("{")) {
					final Object tempObj = mapper.readValue(jsonString_, Object.class);
					return mapper.writeValueAsString(tempObj);
				} else {
					final Object[] tempObj = mapper.readValue(jsonString_, Object[].class);
					return mapper.writeValueAsString(tempObj);
				}
			}
		} catch (final IOException ex) {
			// it seems it is not a JSON string, so we just return untouched
		}

		return jsonString;
	}

	//-------------------------------------------------------------------------------------------------
	@Nullable
	public static String stripEndSlash(final String uri) {
		if (uri != null && uri.endsWith("/")) {
			return uri.substring(0, uri.length() - 1);
		}

		return uri;
	}

	//-------------------------------------------------------------------------------------------------
	public static String convertZonedDateTimeToUTCString(final ZonedDateTime time) {
		if (time == null) {
			return null;
		}

		return dateTimeFormatter.format(time.toInstant());
	}

	//-------------------------------------------------------------------------------------------------
	public static ZonedDateTime parseUTCStringToLocalZonedDateTime(final String timeStr) throws DateTimeParseException {
		if (isEmpty(timeStr)) {
			return null;
		}

		final TemporalAccessor tempAcc = dateTimeFormatter.parse(timeStr);

		return ZonedDateTime.ofInstant(Instant.from(tempAcc), ZoneId.systemDefault());
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private Utilities() {
		throw new UnsupportedOperationException();
	}
}
