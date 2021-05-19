package com.github.springlink.basic.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class SecurityUtils {
	public static Optional<OAuth2AccessToken> getCurrentToken() {
		return getBearerTokenAuthentication()
				.map(auth -> auth.getToken());
	}

	public static Optional<String> getCurrentUserId() {
		return getOAuth2AuthenticatedPrincipal()
				.map(prin -> (String) prin.getAttribute(OAuth2IntrospectionClaimNames.SUBJECT));
	}

	private static Optional<BearerTokenAuthentication> getBearerTokenAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth instanceof BearerTokenAuthentication) {
			return Optional.of((BearerTokenAuthentication) auth);
		}
		return Optional.empty();
	}

	private static Optional<OAuth2AuthenticatedPrincipal> getOAuth2AuthenticatedPrincipal() {
		return getBearerTokenAuthentication().flatMap(auth -> {
			Object prin = auth.getPrincipal();
			if (prin instanceof OAuth2AuthenticatedPrincipal) {
				return Optional.of((OAuth2AuthenticatedPrincipal) prin);
			}
			return Optional.empty();
		});
	}
}
