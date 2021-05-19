package com.github.springlink.basic.module.sys.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.springlink.basic.core.ApiException;
import com.github.springlink.basic.module.sys.dao.UserRepository;
import com.github.springlink.basic.module.sys.domain.User;
import com.github.springlink.basic.module.sys.dto.MyProfileReply;
import com.github.springlink.basic.module.sys.dto.MySetPassword;
import com.github.springlink.basic.module.sys.dto.MySetProfile;
import com.github.springlink.basic.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyService {
	private final UserMapper userMapper;
	private final UserRepository userRepository;

	public MyProfileReply profile() {
		return userMapper.entityToMyProfileReply(
				SecurityUtils.getCurrentUserId()
						.flatMap(userRepository::findByIdAndDeletedFalse)
						.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND")));
	}

	@Transactional
	public void setProfile(@Valid MySetProfile body) {
		userMapper.mySetProfileToEntity(
				body,
				SecurityUtils.getCurrentUserId()
						.flatMap(userRepository::findByIdAndDeletedFalse)
						.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND")));
	}

	@Transactional
	public void setPassword(@Valid MySetPassword body) {
		User user = SecurityUtils.getCurrentUserId()
				.flatMap(userRepository::findByIdAndDeletedFalse)
				.orElseThrow(() -> new ApiException("OBJECT_NOT_FOUND"));
		if (!user.passwordMatches(body.getPassword())) {
			throw new ApiException("INCORRECT_PASSWORD");
		}
		user.setPassword(body.getNewPassword());
	}
}
