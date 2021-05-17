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
import com.github.springlink.basic.module.sys.dao.AccountRepository;
import com.github.springlink.basic.module.sys.dao.AccountTokenRepository;
import com.github.springlink.basic.module.sys.dao.RoleRepository;
import com.github.springlink.basic.module.sys.domain.Account;
import com.github.springlink.basic.module.sys.domain.AccountToken;
import com.github.springlink.basic.module.sys.dto.AccountAuth;
import com.github.springlink.basic.module.sys.dto.AccountLogin;
import com.github.springlink.basic.module.sys.dto.AccountLoginReply;
import com.google.common.collect.Maps;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountTokenService implements OpaqueTokenIntrospector {
	private final AccountMapper accountMapper;
	private final AccountRepository accountRepository;
	private final AccountTokenRepository accountTokenRepository;
	private final RoleRepository roleRepository;
	private final ObjectMapper objectMapper;

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		AccountToken accountToken = accountTokenRepository.findById(token)
				.filter(AccountToken::isValidNow)
				.orElseThrow(() -> new OAuth2IntrospectionException("invalid token"));

		AccountAuth authReply;
		try {
			authReply = objectMapper.readValue(accountToken.getData(), AccountAuth.class);
		} catch (JsonProcessingException e) {
			throw new OAuth2IntrospectionException("IO error");
		}

		Map<String, Object> attributes = Maps.newHashMap();
		attributes.put(OAuth2IntrospectionClaimNames.SUBJECT, authReply.getUserId());
		attributes.put(OAuth2IntrospectionClaimNames.ISSUED_AT,
				accountToken.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
		attributes.put(OAuth2IntrospectionClaimNames.EXPIRES_AT,
				accountToken.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant());

		List<GrantedAuthority> authorities = authReply
				.getPermissions()
				.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
	}

	@Transactional
	public AccountLoginReply login(AccountLogin login) {
		Account account = accountRepository.findByUsernameAndDeletedFalse(login.getUsername())
				.orElseThrow(() -> new ApplicationException("INCORRECT_LOGIN_INFO"));
		if (!account.passwordMatches(login.getPassword())) {
			throw new ApplicationException("INCORRECT_LOGIN_INFO");
		}

		AccountAuth authReply = accountMapper.entityToAuthReply(account);
		authReply.setPermissions(roleRepository
				.findAllByIdInAndDeletedFalse(account.getRoleIds())
				.stream()
				.flatMap(r -> r.getPermissions().stream())
				.collect(Collectors.toSet()));

		AccountToken accountToken;
		try {
			accountToken = new AccountToken(account.getId(), Duration.ofHours(1),
					objectMapper.writeValueAsString(authReply));
		} catch (JsonProcessingException e) {
			throw new ApplicationException("IO_ERROR");
		}

		accountTokenRepository.save(accountToken);

		AccountLoginReply loginReply = accountMapper.tokenToLoginReply(accountToken);
		loginReply.setAuth(authReply);
		return loginReply;
	}

	public AccountAuth getAuthByToken(String token) {
		AccountToken accountToken = accountTokenRepository.findById(token)
				.filter(AccountToken::isValidNow)
				.orElseThrow(() -> new ApplicationException("INVALID_TOKEN"));
		try {
			return objectMapper.readValue(accountToken.getData(), AccountAuth.class);
		} catch (JsonProcessingException e) {
			throw new ApplicationException("IO_ERROR");
		}
	}
}
