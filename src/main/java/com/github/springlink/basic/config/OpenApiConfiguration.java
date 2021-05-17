package com.github.springlink.basic.config;

import static org.springdoc.core.SpringDocUtils.getConfig;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@OpenAPIDefinition(info = @Info(title = "BasicApplication", version = "1.0"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "accessToken")
public class OpenApiConfiguration {
	static {
		getConfig()
				.addRequestWrapperToIgnore(Pageable.class);
	}

	@Bean
	public OpenApiCustomiser openApiCustomiser() {
		PathMatcher matcher = new AntPathMatcher();
		return c -> {
			c.getPaths().forEach((path, item) -> {
				if (matcher.match("/api/account/auth/login", path)) {
					return;
				}
				if (matcher.match("/api/**", path)) {
					Operation[] ops = new Operation[] {
							item.getGet(), item.getPost(), item.getDelete(), item.getHead(),
							item.getOptions(), item.getPatch(), item.getPut()
					};
					Stream.of(ops)
							.filter(Objects::nonNull)
							.forEach(op -> {
								op.setSecurity(Arrays.asList(
										new SecurityRequirement().addList("bearerAuth")));
							});
				}
			});
		};
	}
}