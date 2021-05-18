package com.github.springlink.basic.module.sys.service;

import java.time.Duration;
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
import com.github.springlink.basic.core.ApplicationException;
import com.github.springlink.basic.module.sys.dao.RoleRepository;
import com.github.springlink.basic.module.sys.dao.UserRepository;
import com.github.springlink.basic.module.sys.dao.UserTokenRepository;
import com.github.springlink.basic.module.sys.domain.User;
import com.github.springlink.basic.module.sys.domain.UserToken;
import com.github.springlink.basic.module.sys.dto.UserAuth;
import com.github.springlink.basic.module.sys.dto.UserLogin;
import com.github.springlink.basic.module.sys.dto.UserLoginReply;
import com.google.common.collect.Maps;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTokenService implements OpaqueTokenIntrospector {
	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final UserTokenRepository userTokenRepository;
	private final RoleRepository roleRepository;
	private final ObjectMapper objectMapper;

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		UserToken userToken = userTokenRepository.findById(token)
				.filter(UserToken::isValidNow)
				.orElseThrow(() -> new OAuth2IntrospectionException("invalid token"));

		UserAuth userAuth;
		try {
			userAuth = objectMapper.readValue(userToken.getData(), UserAuth.class);
		} catch (JsonProcessingException e) {
			throw new OAuth2IntrospectionException("IO error");
		}

		Map<String, Object> attributes = Maps.newHashMap();
		attributes.put(OAuth2IntrospectionClaimNames.SUBJECT, userAuth.getUserId());
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
		User user = userRepository.findByUsernameAndDeletedFalse(userLogin.getUsername())
				.orElseThrow(() -> new ApplicationException("INCORRECT_LOGIN_INFO"));
		if (!user.passwordMatches(userLogin.getPassword())) {
			throw new ApplicationException("INCORRECT_LOGIN_INFO");
		}

		UserAuth userAuth = userMapper.entityToAuth(user);
		userAuth.setPermissions(roleRepository
				.findAllByIdInAndDeletedFalse(user.getRoleIds())
				.stream()
				.flatMap(r -> r.getPermissions().stream())
				.collect(Collectors.toSet()));

		UserToken userToken;
		try {
			userToken = userTokenRepository.save(
					new UserToken(user.getId(), Duration.ofHours(1),
							objectMapper.writeValueAsString(userAuth)));
		} catch (JsonProcessingException e) {
			throw new ApplicationException("IO_ERROR");
		}

		UserLoginReply userLoginReply = userMapper.tokenToLoginReply(userToken);
		userLoginReply.setAuth(userAuth);
		return userLoginReply;
	}

	public UserAuth getAuthByToken(String token) {
		UserToken userToken = userTokenRepository.findById(token)
				.filter(UserToken::isValidNow)
				.orElseThrow(() -> new ApplicationException("INVALID_TOKEN"));
		try {
			return objectMapper.readValue(userToken.getData(), UserAuth.class);
		} catch (JsonProcessingException e) {
			throw new ApplicationException("IO_ERROR");
		}
	}
}
