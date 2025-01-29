package eu.arrowhead.common.service.validation.meta;

public record MetadataRequirementExpression(
		String keyPath,
		MetaOps operation,
		Object value) {
}