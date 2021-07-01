package sourcefx.module.sys.dao;

import java.util.Optional;

import sourcefx.core.data.BaseRepository;
import sourcefx.module.sys.domain.User;

public interface UserRepository extends BaseRepository<User> {
	Optional<User> findByUsername(String username);
	
	boolean existsByUsername(String username);
}
