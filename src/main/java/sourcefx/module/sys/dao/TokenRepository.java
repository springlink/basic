package sourcefx.module.sys.dao;

import org.springframework.data.repository.CrudRepository;

import sourcefx.module.sys.domain.Token;

public interface TokenRepository extends CrudRepository<Token, String> {
}
