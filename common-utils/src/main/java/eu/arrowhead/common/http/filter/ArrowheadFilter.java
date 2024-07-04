package eu.arrowhead.common.http.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.Constants;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.http.HttpUtilities;
import eu.arrowhead.dto.ErrorMessageDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class ArrowheadFilter extends OncePerRequestFilter {

	//=================================================================================================
	// members

	@Autowired
	protected ObjectMapper mapper;

	protected final Logger log = LogManager.getLogger(getClass());

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	protected ArrowheadFilter() {
		log.info("{} is active", this.getClass().getSimpleName());
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		final String path = request.getRequestURI();
		return path.equals("/") || path.startsWith(Constants.SWAGGER_API_DOCS_URI) || path.startsWith(Constants.SWAGGER_UI_URI);
	}

	//-------------------------------------------------------------------------------------------------
	protected void handleException(final ArrowheadException ex, final ServletResponse response) throws IOException {
		final HttpStatus status = HttpUtilities.calculateHttpStatusFromArrowheadException(ex);
		final String origin = ex.getOrigin() == null ? Constants.UNKNOWN : ex.getOrigin();
		log.debug("{} at {}: {}", ex.getClass().getName(), origin, ex.getMessage());
		log.debug("Exception", ex);
		final ErrorMessageDTO dto = HttpUtilities.createErrorMessageDTO(ex);
		sendError(status, dto, (HttpServletResponse) response);
	}

	//-------------------------------------------------------------------------------------------------
	protected void sendError(final HttpStatus status, final ErrorMessageDTO dto, final HttpServletResponse response) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(status.value());
		response.getWriter().print(mapper.writeValueAsString(dto));
		response.getWriter().flush();
	}
}