package sourcefx.core.data;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class BaseRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
		extends JpaRepositoryFactoryBean<T, S, ID> {

	public BaseRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		BaseRepositoryFactory factory = new BaseRepositoryFactory(entityManager);
		factory.setEntityPathResolver(readSuperFieldValue("entityPathResolver"));
		factory.setEscapeCharacter(readSuperFieldValue("escapeCharacter"));

		JpaQueryMethodFactory queryMethodFactory = readSuperFieldValue("queryMethodFactory");
		if (queryMethodFactory != null) {
			factory.setQueryMethodFactory(queryMethodFactory);
		}

		return factory;
	}

	@SuppressWarnings("unchecked")
	private <E> E readSuperFieldValue(String name) {
		try {
			Field field = JpaRepositoryFactoryBean.class.getDeclaredField(name);
			field.setAccessible(true);
			return (E) field.get(this);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to read field value from " + JpaRepositoryFactoryBean.class, e);
		}
	}

}
