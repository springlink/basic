package sourcefx.module.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sourcefx.core.AppError;
import sourcefx.core.AppException;
import sourcefx.core.AppUtils;
import sourcefx.module.sys.dao.UserRepository;
import sourcefx.module.sys.domain.User;
import sourcefx.module.sys.dto.my.MyProfileReply;
import sourcefx.module.sys.dto.my.MySetPassword;
import sourcefx.module.sys.dto.my.MySetProfile;

@Service
@RequiredArgsConstructor
public class MyService {
	private final AppUtils appUtils;
	private final UserConverter userConverter;
	private final UserRepository userRepository;

	public MyProfileReply profile() {
		return userConverter.entityToMyProfileReply(
				userRepository.findById(appUtils.getCurrentUserId().orElse(null))
						.orElseThrow(AppError.ENTITY_NOT_FOUND::newException));
	}

	@Transactional
	public void setProfile(MySetProfile body) {
		userConverter.mySetProfileToEntity(
				body,
				userRepository.findById(appUtils.getCurrentUserId().orElse(null))
						.orElseThrow(AppError.ENTITY_NOT_FOUND::newException));
	}

	@Transactional
	public void setPassword(MySetPassword body) {
		User user = userRepository.findById(appUtils.getCurrentUserId().orElse(null))
				.orElseThrow(AppError.ENTITY_NOT_FOUND::newException);
		if (!user.passwordMatches(body.getPassword())) {
			throw new AppException("INCORRECT_PASSWORD");
		}
		user.setPassword(body.getNewPassword());
	}
}
