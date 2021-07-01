package sourcefx.config;

import static org.springdoc.core.SpringDocUtils.getConfig;

import java.util.Arrays;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import com.google.common.net.HttpHeaders;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@OpenAPIDefinition(
		info = @Info(title = "SourceFX"))
@SecurityScheme(
		name = "user",
		type = SecuritySchemeType.HTTP,
		scheme = "Bearer",
		paramName = HttpHeaders.AUTHORIZATION,
		in = SecuritySchemeIn.HEADER,
		bearerFormat = "accessToken")
public class OpenApiConfiguration {
	static {
		getConfig()
				.addRequestWrapperToIgnore(Pageable.class);
	}

	@Bean
	public GroupedOpenApi adminApi() {
		return GroupedOpenApi.builder()
				.group("后台管理")
				.pathsToMatch("/api/**")
				.addOperationCustomizer((operation, handlerMethod) -> {
					operation.setSecurity(Arrays.asList(new SecurityRequirement().addList("user")));
					return operation;
				})
				.build();
	}
}