package sourcefx.core.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;
import sourcefx.Application;
import sourcefx.core.AppException;

@Slf4j
@ControllerAdvice
public class ApiControllerAdvice implements ResponseBodyAdvice<Object> {
	private final MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType)
				&& returnType.getContainingClass().getName().startsWith(Application.class.getPackage().getName());
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		ApiResult<?> result;
		if (body instanceof ApiResult) {
			result = (ApiResult<?>) body;
		} else if (body instanceof Page) {
			result = ApiResult.ok((Page<?>) body);
		} else {
			result = ApiResult.ok(body);
		}
		return new MappingJacksonValue(result);
	}

	@ResponseBody
	@ExceptionHandler(AppException.class)
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> handleAppException(AppException ex) {
		log.debug(ex.getMessage(), ex);
		return ApiResult.error(ex.getCode(), ex.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.debug(ex.getMessage(), ex);
		BindingResult bindingResult = ex.getBindingResult();
		return ApiResult.error("BAD_REQUEST", "请求参数验证失败", bindingResult.getAllErrors().stream()
				.map(err -> {
					if (err instanceof FieldError) {
						FieldError ferr = (FieldError) err;
						return messageCodesResolver.resolveMessageCodes(
								ferr.getCode(), ferr.getObjectName(), ferr.getField(), null)[1];
					} else {
						return messageCodesResolver.resolveMessageCodes(
								err.getCode(), err.getObjectName())[1];
					}
				})
				.collect(Collectors.toList()));
	}
}
