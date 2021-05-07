package com.github.springlink.basic.module.sys.service;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.springlink.basic.core.ApplicationException;
import com.github.springlink.basic.module.sys.domain.Account;
import com.github.springlink.basic.module.sys.domain.AccountToken;
import com.github.springlink.basic.module.sys.dto.AccountLogin;
import com.github.springlink.basic.module.sys.dto.AccountLoginReply;
import com.github.springlink.basic.module.sys.repository.AccountRepository;
import com.github.springlink.basic.module.sys.repository.AccountTokenRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountTokenService implements OpaqueTokenIntrospector {
	private final AccountMapper accountMapper;
	private final AccountRepository accountRepository;
	private final AccountTokenRepository accountTokenRepository;

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		AccountToken accountToken = accountTokenRepository.findById(token)
				.orElseThrow(() -> new OAuth2IntrospectionException("invalid token"));
		if (!accountToken.isValidNow()) {
			throw new OAuth2IntrospectionException("invalid token");
		}
		Account account = accountRepository.findByIdAndDeletedFalse(accountToken.getUserId())
				.orElseThrow(() -> new OAuth2IntrospectionException("account unavailable"));

		Map<String, Object> attributes = Maps.newHashMap();
		attributes.put(OAuth2IntrospectionClaimNames.SUBJECT, account.getId());
		attributes.put(OAuth2IntrospectionClaimNames.ISSUED_AT,
				accountToken.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
		attributes.put(OAuth2IntrospectionClaimNames.EXPIRES_AT,
				accountToken.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant());

		List<GrantedAuthority> authorities = Lists.newArrayList();

		return new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
	}

	@Transactional
	public AccountLoginReply login(AccountLogin login) {
		Account account = accountRepository.findByUsernameAndDeletedFalse(login.getUsername())
				.orElseThrow(() -> new ApplicationException("INCORRECT_LOGIN_INFO"));
		if (!account.passwordMatches(login.getPassword())) {
			throw new ApplicationException("INCORRECT_LOGIN_INFO");
		}
		AccountToken accountToken = new AccountToken(account.getId(), Duration.ofHours(1));
		accountTokenRepository.save(accountToken);
		return accountMapper.entityToLoginReply(account, accountToken);
	}
}
