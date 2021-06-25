package sourcefx.core;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppUtils implements ApplicationContextAware, DisposableBean {
	private static AppUtils instance;

	public static AppUtils getInstance() {
		if (instance == null) {
			throw new IllegalStateException("ApplicationUtils instance unavailable");
		}
		return instance;
	}

	@Getter
	private final ApplicationContext applicationContext;

	@Getter
	private final EntityManager entityManager;

	@Override
	public synchronized void setApplicationContext(ApplicationContext ctx) throws BeansException {
		if (instance != null) {
			throw new IllegalStateException("Multiple AppUtils instance is now allowed");
		}
		instance = this;
	}

	@Override
	public void destroy() throws Exception {
		instance = null;
	}

	public Object getBean(String name) throws BeansException {
		return applicationContext.getBean(name);
	}

	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(name, requiredType);
	}

	public Object getBean(String name, Object... args) throws BeansException {
		return applicationContext.getBean(name, args);
	}

	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(requiredType);
	}

	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return applicationContext.getBean(requiredType, args);
	}

	public Optional<OAuth2AccessToken> getCurrentToken() {
		return getBearerTokenAuthentication()
				.map(auth -> auth.getToken());
	}

	public Optional<Long> getCurrentUserId() {
		return getOAuth2AuthenticatedPrincipal()
				.map(prin -> Long.parseLong((String) prin.getAttribute(OAuth2IntrospectionClaimNames.SUBJECT)));
	}

	private Optional<BearerTokenAuthentication> getBearerTokenAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		if (auth instanceof BearerTokenAuthentication) {
			return Optional.of((BearerTokenAuthentication) auth);
		}
		return Optional.empty();
	}

	private Optional<OAuth2AuthenticatedPrincipal> getOAuth2AuthenticatedPrincipal() {
		return getBearerTokenAuthentication().flatMap(auth -> {
			Object prin = auth.getPrincipal();
			if (prin instanceof OAuth2AuthenticatedPrincipal) {
				return Optional.of((OAuth2AuthenticatedPrincipal) prin);
			}
			return Optional.empty();
		});
	}
}
