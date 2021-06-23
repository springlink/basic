package sourcefx.core.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springdoc.core.ReturnTypeParser;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApiReturnTypeParser implements ReturnTypeParser {
	@Override
	public Type getReturnType(MethodParameter methodParameter) {
		if (methodParameter.getGenericParameterType() instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) methodParameter.getGenericParameterType();
			if (Page.class.isAssignableFrom((Class<?>) paramType.getRawType())) {
				paramType = newParameterizedType(ApiPage.class, paramType.getActualTypeArguments());
			}
			return newParameterizedType(ApiResult.class, ReturnTypeParser.resolveType(
					paramType, methodParameter.getContainingClass()));
		}
		Class<?> type = methodParameter.getParameterType();
		if (type == void.class) {
			type = Void.class;
		}
		return newParameterizedType(ApiResult.class, type);
	}

	private ParameterizedType newParameterizedType(Type rawType, Type... typeArgs) {
		return new ParameterizedType() {
			public Type[] getActualTypeArguments() {
				return typeArgs;
			}

			public Type getRawType() {
				return rawType;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}
}
