package sourcefx.module.sys.dao;

import java.util.Optional;

import sourcefx.core.data.BaseRepository;
import sourcefx.module.sys.domain.UserToken;

public interface UserTokenRepository extends BaseRepository<UserToken> {
	Optional<UserToken> findByToken(String token);
}
