package sourcefx.module.sys.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import sourcefx.module.sys.domain.User;

public interface UserRepository extends PagingAndSortingRepository<User, String>, QuerydslPredicateExecutor<User> {
	Optional<User> findByUsername(String username);

	Optional<User> findByUsernameAndDeletedFalse(String username);

	Optional<User> findByIdAndDeletedFalse(String id);

	Page<User> findAllByDeletedFalse(Pageable pageable);
}
