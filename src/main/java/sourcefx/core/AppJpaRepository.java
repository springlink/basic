package sourcefx.core;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public class AppJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {
	private final JpaEntityInformation<T, ID> entityInformation;
	private final EntityManager entityManager;

	public AppJpaRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityInformation = entityInformation;
		this.entityManager = entityManager;
	}

	@Override
	protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, Class<S> domainClass, Sort sort) {
		SingularAttribute<? super T, ?> deletedAttr = getDeletedAttribute();
		if (deletedAttr != null) {
			Specification<S> additional = (root, query, criteriaBuilder) -> {
				return criteriaBuilder.equal(root.get(deletedAttr), false);
			};
			return super.getQuery(spec.and(additional), domainClass, sort);
		}
		return super.getQuery(spec, domainClass, sort);
	}

	private SingularAttribute<? super T, ?> getDeletedAttribute() {
		ManagedType<T> managedType = entityManager.getMetamodel().managedType(entityInformation.getJavaType());
		for (SingularAttribute<? super T, ?> attr : managedType.getSingularAttributes()) {
			if ("deleted".equals(attr.getName())
					&& (boolean.class == attr.getJavaType() || Boolean.class == attr.getJavaType())) {
				return attr;
			}
		}
		return null;
	}
}
