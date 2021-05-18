package com.github.springlink.basic.module.sys.service;

import java.util.Collections;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.springlink.basic.core.ApplicationException;
import com.github.springlink.basic.module.sys.dao.UserRepository;
import com.github.springlink.basic.module.sys.domain.User;
import com.github.springlink.basic.module.sys.dto.UserAdd;
import com.github.springlink.basic.module.sys.dto.UserChangeProfile;
import com.github.springlink.basic.module.sys.dto.UserReply;

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
			userRepository.save(new User("root", "123456", null, null, Collections.emptySet()));
		}
	}

	@Transactional
	public UserReply add(UserAdd userAdd) {
		return userMapper.entityToReply(userRepository.save(userMapper.addToEntity(userAdd)));
	}

	@Transactional
	public void changeProfile(UserChangeProfile userChangeProfile) {
		User user = userRepository.findByIdAndDeletedFalse(userChangeProfile.getId())
				.orElseThrow(() -> new ApplicationException("OBJECT_NOT_FOUND"));
		userMapper.changeProfileToEntity(userChangeProfile, user);
	}

	@Transactional
	public void delete(String id) {
		User user = userRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new ApplicationException("OBJECT_NOT_FOUND"));
		user.markDeleted();
	}

	public Page<UserReply> page(Pageable pageable) {
		return userRepository.findAllByDeletedFalse(pageable).map(userMapper::entityToReply);
	}
}
