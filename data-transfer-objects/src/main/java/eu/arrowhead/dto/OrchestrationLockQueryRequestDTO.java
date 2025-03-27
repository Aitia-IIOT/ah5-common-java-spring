package eu.arrowhead.dto;

import java.util.List;

public record OrchestrationLockQueryRequestDTO(
		PageDTO pagination,
		List<Long> ids,
		List<String> orchestrationJobIds,
		List<String> serviceInstanceIds,
		List<String> owners,
		String expiresBefore,
		String expiresAfter) {

}
