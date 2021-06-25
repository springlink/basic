package sourcefx.module.sys.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.RequiredArgsConstructor;
import sourcefx.core.AppException;
import sourcefx.module.sys.dao.RoleRepository;
import sourcefx.module.sys.dao.UserTokenRepository;
import sourcefx.module.sys.dao.UserRepository;
import sourcefx.module.sys.domain.QUser;
import sourcefx.module.sys.domain.QUserToken;
import sourcefx.module.sys.domain.User;
import sourcefx.module.sys.domain.UserToken;
import sourcefx.module.sys.dto.auth.UserAuth;
import sourcefx.module.sys.dto.auth.UserLogin;
import sourcefx.module.sys.dto.auth.UserLoginReply;

@Service
@RequiredArgsConstructor
public class UserTokenService implements OpaqueTokenIntrospector {
	private final UserConverter userConverter;
	private final UserRepository userRepository;
	private final UserTokenRepository userTokenRepository;
	private final RoleRepository roleRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		UserToken userToken = userTokenRepository.findOne(QUserToken.userToken.token.eq(token))
				.filter(UserToken::isValidNow)
				.orElseThrow(() -> new OAuth2IntrospectionException("invalid token"));

		userToken.touch(LocalDateTime.now().plus(Duration.ofMinutes(30)));

		UserAuth userAuth;
		try {
			userAuth = objectMapper.readValue(userToken.getData(), UserAuth.class);
		} catch (JsonProcessingException e) {
			throw new OAuth2IntrospectionException("IO error");
		}

		Map<String, Object> attributes = Maps.newHashMap();
		attributes.put(OAuth2IntrospectionClaimNames.SUBJECT, String.valueOf(userAuth.getUserId()));
		attributes.put(OAuth2IntrospectionClaimNames.ISSUED_AT,
				userToken.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
		attributes.put(OAuth2IntrospectionClaimNames.EXPIRES_AT,
				userToken.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant());

		List<GrantedAuthority> authorities = userAuth
				.getPermissions()
				.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
	}

	@Transactional
	public UserLoginReply login(UserLogin userLogin) {
		User user = userRepository.findOne(QUser.user.username.eq(userLogin.getUsername()))
				.orElseThrow(() -> new AppException("INCORRECT_LOGIN_INFO"));
		if (!user.passwordMatches(userLogin.getPassword())) {
			throw new AppException("INCORRECT_LOGIN_INFO");
		}
		if (user.isLocked()) {
			throw new AppException("USER_LOCKED");
		}

		UserAuth userAuth = userConverter.entityToAuth(user);
		userAuth.setPermissions(
				Lists.newArrayList(roleRepository.findAllById(user.getRoleIds()))
						.stream()
						.flatMap(r -> r.getPermissions().stream())
						.collect(Collectors.toSet()));

		UserToken userToken;
		try {
			userToken = userTokenRepository.save(
					new UserToken(user.getId(), Duration.ofHours(1),
							objectMapper.writeValueAsString(userAuth)));
		} catch (JsonProcessingException e) {
			throw new AppException("IO_ERROR");
		}

		UserLoginReply userLoginReply = userConverter.tokenToLoginReply(userToken);
		userLoginReply.setAuth(userAuth);
		return userLoginReply;
	}

	public UserAuth getAuthByToken(String token) {
		UserToken userToken = userTokenRepository.findOne(QUserToken.userToken.token.eq(token))
				.filter(UserToken::isValidNow)
				.orElseThrow(() -> new AppException("INVALID_TOKEN"));
		try {
			return objectMapper.readValue(userToken.getData(), UserAuth.class);
		} catch (JsonProcessingException e) {
			throw new AppException("IO_ERROR");
		}
	}
}
