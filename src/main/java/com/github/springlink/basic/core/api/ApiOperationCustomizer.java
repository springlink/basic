package com.github.springlink.basic.core.api;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

@Component
public class ApiOperationCustomizer implements OperationCustomizer {
	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {
		if (hasPageableParameter(handlerMethod)) {
			operation.addParametersItem(new Parameter()
					.in("query")
					.name("page")
					.description("Page number, start from 1")
					.schema(new IntegerSchema()._default(1)));
			operation.addParametersItem(new Parameter()
					.in("query")
					.name("size")
					.description("Page size")
					.schema(new IntegerSchema()._default(20)));
			operation.addParametersItem(new Parameter()
					.in("query")
					.name("sort")
					.description("Sort expression, format: {field},(asc|desc)")
					.schema(new StringSchema()._default("")));
		}
		return operation;
	}

	private boolean hasPageableParameter(HandlerMethod handlerMethod) {
		for (MethodParameter param : handlerMethod.getMethodParameters()) {
			if (Pageable.class.isAssignableFrom(param.getParameterType())) {
				return true;
			}
		}
		return false;
	}

}
