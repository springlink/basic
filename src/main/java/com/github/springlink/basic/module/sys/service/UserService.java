package com.github.springlink.basic.module.sys.service;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.springlink.basic.core.ApiException;
import com.github.springlink.basic.module.sys.dao.UserRepository;
import com.github.springlink.basic.module.sys.domain.QUser;
import com.github.springlink.basic.module.sys.domain.User;
import com.github.springlink.basic.module.sys.dto.UserAdd;
import com.github.springlink.basic.module.sys.dto.UserQuery;
import com.github.springlink.basic.module.sys.dto.UserReply;
import com.github.springlink.basic.module.sys.dto.UserSetLocked;
import com.github.springlink.basic.module.sys.dto.UserSetPassword;
import com.github.springlink.basic.module.sys.dto.UserSetProfile;
import com.github.springlink.basic.util.SecurityUtils;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;
	private final UserRepository userRepository;

	@Transactional
	@EventListener
	public void onContextRefreshed(ContextRefreshedEvent event) {
		if (!userRepository.findByUsernameAndDeletedFalse("root").isPresent()) {
			userRepository.save(new User("root", "123456", true));
		}
	}

	@Transactional
	public UserReply add(UserAdd add) {
		return userMapper.entityToReply(userRepository.save(userMapper.addToEntity(add)));
	}

	@Transactional
	public void setPassword(UserSetPassword setPassword) {
		userRepository.findByIdAndDeletedFalse(setPassword.getId())
				.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND"))
				.setPassword(setPassword.getPassword());
	}

	@Transactional
	public void setProfile(UserSetProfile setProfile) {
		userMapper.setProfileToEntity(
				setProfile,
				userRepository.findByIdAndDeletedFalse(setProfile.getId())
						.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND")));
	}

	@Transactional
	public void setLocked(UserSetLocked setLocked) {
		userRepository.findByIdAndDeletedFalse(setLocked.getId())
				.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND"))
				.setLocked(setLocked.getLocked());
	}

	@Transactional
	public void delete(String id) {
		userRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND"))
				.delete();
	}

	public Page<UserReply> page(UserQuery query, Pageable pageable) {
		BooleanBuilder bb = new BooleanBuilder();
		if (StringUtils.hasText(query.getUsername())) {
			bb.and(QUser.user.username.containsIgnoreCase(query.getUsername()));
		}
		if (query.getCreatedDate() != null) {
			if (query.getCreatedDate().length > 0 && query.getCreatedDate()[0] != null) {
				bb.and(QUser.user.createdDate.goe(query.getCreatedDate()[0]));
			}
			if (query.getCreatedDate().length > 1 && query.getCreatedDate()[1] != null) {
				bb.and(QUser.user.createdDate.loe(query.getCreatedDate()[1]));
			}
		}
		SecurityUtils.getCurrentUserId().ifPresent(id -> bb.and(QUser.user.id.ne(id)));
		bb.and(QUser.user.builtIn.isFalse());
		bb.and(QUser.user.deleted.isFalse());
		return userRepository.findAll(bb, pageable).map(userMapper::entityToReply);
	}
}
