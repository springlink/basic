package com.github.springlink.basic.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class SecurityUtils {
	public static Optional<OAuth2AccessToken> currentToken() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (!(auth instanceof BearerTokenAuthentication)) {
			return Optional.empty();
		}
		BearerTokenAuthentication tokenAuth = (BearerTokenAuthentication) auth;
		Object prin = auth.getPrincipal();
		if (!(prin instanceof OAuth2AuthenticatedPrincipal)) {
			return Optional.empty();
		}
		return Optional.of(tokenAuth.getToken());
	}
}
