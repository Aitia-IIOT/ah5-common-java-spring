package eu.arrowhead.dto;
import java.util.List;

public record BlacklistQueryRequestDTO(
		PageDTO pagination,
		List<String> systemNames,
		String mode,
		List<String> issuers,
		List<String> revokers,
		String reason,
		String alivesAt) {
}
