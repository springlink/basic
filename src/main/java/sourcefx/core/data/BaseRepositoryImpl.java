package sourcefx.core.data;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryHints;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;

@NoRepositoryBean
public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID>
		implements QuerydslPredicateExecutor<T> {

	private final JpaEntityInformation<T, ?> entityInformation;
	private final EntityManager entityManager;
	private final PathBuilder<T> path;
	private final PathBuilder<Object> pathOfId;
	private final BooleanPath pathOfDeleted;
	private final Querydsl querydsl;

	public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.entityInformation = entityInformation;
		this.entityManager = entityManager;

		this.path = new PathBuilderFactory().create(entityInformation.getJavaType());
		this.pathOfId = path.get(this.entityInformation.getRequiredIdAttribute().getName());
		this.pathOfDeleted = getPathForDeleted(this.path, this.entityManager);

		this.querydsl = new Querydsl(entityManager, this.path);
	}

	@Override
	public Optional<T> findOne(Predicate predicate) {
		try {
			return Optional.ofNullable(createQuery(predicate).fetchOne());
		} catch (NonUniqueResultException ex) {
			throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
		}
	}

	@Override
	public Optional<T> findById(ID id) {
		return findOne(pathOfId.eq(id));
	}

	@Override
	public Iterable<T> findAll(Predicate predicate) {
		return createQuery(predicate).fetch();
	}

	@Override
	public Iterable<T> findAll(Predicate predicate, Sort sort) {
		return querydsl.applySorting(sort, createQuery(predicate)).fetch();
	}

	@Override
	public Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
		return findAll(predicate, new QSort(orders));
	}

	@Override
	public Iterable<T> findAll(OrderSpecifier<?>... orders) {
		return findAll(null, orders);
	}

	@Override
	public Page<T> findAll(Predicate predicate, Pageable pageable) {
		return PageableExecutionUtils.getPage(
				querydsl.applyPagination(pageable, createQuery(predicate)).fetch(),
				pageable, () -> count(predicate));
	}

	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		return Lists.newArrayList(findAll(pathOfId.in(Lists.newArrayList(ids))));
	}

	@Override
	public long count(Predicate predicate) {
		return createCountQuery(predicate).fetchCount();
	}

	@Override
	public boolean exists(Predicate predicate) {
		return count(predicate) > 0;
	}

	@Override
	public boolean existsById(ID id) {
		return exists(pathOfId.eq(id));
	}

	private JPQLQuery<T> createQuery(@Nullable Predicate predicate) {
		return createQuery(getQueryHints().withFetchGraphs(entityManager), predicate);
	}

	private JPQLQuery<T> createCountQuery(@Nullable Predicate predicate) {
		return createQuery(getQueryHints().withFetchGraphs(entityManager).forCounts(), predicate);
	}

	private JPQLQuery<T> createQuery(QueryHints hints, @Nullable Predicate predicate) {
		AbstractJPAQuery<T, JPAQuery<T>> query = querydsl.createQuery();
		query.select(path).from(path);

		BooleanBuilder conditions = new BooleanBuilder();
		if (predicate != null) {
			conditions.and(predicate);
		}
		if (pathOfDeleted != null) {
			conditions.and(pathOfDeleted.isFalse());
		}
		query.where(conditions);

		hints.forEach(query::setHint);

		Optional.of(getRepositoryMethodMetadata())
				.map(CrudMethodMetadata::getLockModeType)
				.ifPresent(query::setLockMode);

		return query;
	}

	private BooleanPath getPathForDeleted(PathBuilder<?> path, EntityManager entityManager) {
		return entityManager.getMetamodel().entity(path.getType()).getSingularAttributes()
				.stream()
				.filter(attr -> {
					Member member = attr.getJavaMember();
					if (!(member instanceof AnnotatedElement)) {
						return false;
					}
					Deleted annotation = ((AnnotatedElement) member).getAnnotation(Deleted.class);
					if (annotation == null) {
						return false;
					}
					Class<?> attrType = attr.getJavaType();
					if (boolean.class != attrType && Boolean.class != attrType) {
						throw new IllegalStateException("Deleted attribute supports boolean type only");
					}
					return true;
				})
				.findFirst()
				.map(attr -> path.getBoolean(attr.getName()))
				.orElse(null);
	}
}
