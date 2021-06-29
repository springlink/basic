package sourcefx.core.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends Repository<T, Long> {
	Optional<T> findById(Long id);

	Optional<T> findOne(Predicate predicate);

	List<T> findAllById(Iterable<Long> ids);

	List<T> findAll(Predicate predicate);

	List<T> findAll(Predicate predicate, Sort sort);

	List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);

	Page<T> findAll(Predicate predicate, Pageable pageable);

	long count(Predicate predicate);

	boolean existsById(Long id);

	boolean exists(Predicate predicate);

	<S extends T> S save(S entity);
}
