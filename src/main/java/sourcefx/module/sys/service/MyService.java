package sourcefx.module.sys.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sourcefx.core.AppException;
import sourcefx.core.AppUtils;
import sourcefx.module.sys.dao.UserRepository;
import sourcefx.module.sys.domain.User;
import sourcefx.module.sys.dto.MyProfileReply;
import sourcefx.module.sys.dto.MySetPassword;
import sourcefx.module.sys.dto.MySetProfile;

@Service
@RequiredArgsConstructor
public class MyService {
	private final AppUtils appUtils;
	private final UserMapper userMapper;
	private final UserRepository userRepository;

	public MyProfileReply profile() {
		return userMapper.entityToMyProfileReply(
				appUtils.getCurrentSubject()
						.flatMap(userRepository::findByIdAndDeletedFalse)
						.orElseThrow(() -> new AppException("OBJECT_NOT_FOUND")));
	}

	@Transactional
	public void setProfile(@Valid MySetProfile body) {
		userMapper.mySetProfileToEntity(
				body,
				appUtils.getCurrentSubject()
						.flatMap(userRepository::findByIdAndDeletedFalse)
						.orElseThrow(() -> new AppException("OBJECT_NOT_FOUND")));
	}

	@Transactional
	public void setPassword(@Valid MySetPassword body) {
		User user = appUtils.getCurrentSubject()
				.flatMap(userRepository::findByIdAndDeletedFalse)
				.orElseThrow(() -> new AppException("OBJECT_NOT_FOUND"));
		if (!user.passwordMatches(body.getPassword())) {
			throw new AppException("INCORRECT_PASSWORD");
		}
		user.setPassword(body.getNewPassword());
	}
}
