package sourcefx.core;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
@Transactional(readOnly = true)
public interface BaseRepository<T extends BaseEntity> extends CrudRepository<T, Long>, QuerydslPredicateExecutor<T> {

	@Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.deleted = false")
	@Override
	Optional<T> findById(Long id);

	@Query("SELECT e FROM #{#entityName} e WHERE e.id IN ?1 AND e.deleted = false")
	@Override
	Iterable<T> findAllById(Iterable<Long> ids);

	@Override
	default boolean existsById(Long id) {
		return findById(id).isPresent();
	}

	@Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
	@Override
	Iterable<T> findAll();

	@Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
	@Override
	long count();

	@Modifying
	@Query("UPDATE #{#entityName} e SET deleted = true WHERE e.id = ?1")
	@Transactional
	@Override
	void deleteById(Long id);

	@Transactional
	@Override
	default void delete(T entity) {
		entity.markDeleted();
		save(entity);
	}

	@Transactional
	@Override
	default void deleteAll(Iterable<? extends T> entities) {
		entities.forEach(BaseEntity::markDeleted);
		saveAll(entities);
	}

	@Modifying
	@Query("UPDATE #{#entityName} e SET deleted = true")
	@Transactional
	@Override
	void deleteAll();

}
