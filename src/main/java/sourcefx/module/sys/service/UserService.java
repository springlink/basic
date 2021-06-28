package sourcefx.module.sys.service;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;
import sourcefx.core.AppError;
import sourcefx.core.AppUtils;
import sourcefx.module.sys.dao.UserRepository;
import sourcefx.module.sys.domain.QUser;
import sourcefx.module.sys.domain.User;
import sourcefx.module.sys.dto.user.UserAdd;
import sourcefx.module.sys.dto.user.UserQuery;
import sourcefx.module.sys.dto.user.UserReply;
import sourcefx.module.sys.dto.user.UserSetLocked;
import sourcefx.module.sys.dto.user.UserSetPassword;
import sourcefx.module.sys.dto.user.UserSetProfile;

@Service
@RequiredArgsConstructor
public class UserService {
	private final AppUtils appUtils;
	private final UserConverter userConverter;
	private final UserRepository userRepository;

	@Transactional
	@EventListener
	public void onContextRefreshed(ContextRefreshedEvent event) {
		if (!userRepository.exists(QUser.user.username.eq("root"))) {
			userRepository.save(new User("root", "123456", true));
		}
	}

	@Transactional
	public UserReply add(UserAdd add) {
		return userConverter.convertToReply(userRepository.save(userConverter.convert(add)));
	}

	@Transactional
	public void setPassword(UserSetPassword setPassword) {
		userRepository.findById(setPassword.getId())
				.orElseThrow(AppError.ENTITY_NOT_FOUND::newException)
				.setPassword(setPassword.getPassword());
	}

	@Transactional
	public void setProfile(UserSetProfile setProfile) {
		userConverter.convert(
				setProfile,
				userRepository.findById(setProfile.getId())
						.orElseThrow(AppError.ENTITY_NOT_FOUND::newException));
	}

	@Transactional
	public void setLocked(UserSetLocked setLocked) {
		userRepository.findById(setLocked.getId())
				.orElseThrow(AppError.ENTITY_NOT_FOUND::newException)
				.setLocked(setLocked.getLocked());
	}

	@Transactional
	public void delete(Long id) {
		userRepository.findById(id)
				.orElseThrow(AppError.ENTITY_NOT_FOUND::newException)
				.markDeleted();
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
		appUtils.getCurrentUserId().ifPresent(id -> bb.and(QUser.user.id.ne(id)));
		bb.and(QUser.user.builtIn.isFalse());
		bb.and(QUser.user.deleted.isFalse());
		return userRepository.findAll(bb, pageable).map(userConverter::convertToReply);
	}
}
