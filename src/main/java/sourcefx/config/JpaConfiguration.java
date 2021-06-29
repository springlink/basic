package sourcefx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import sourcefx.Application;
import sourcefx.core.data.BaseRepositoryFactoryBean;

@Configuration
@EnableJpaRepositories(
		basePackageClasses = Application.class,
		repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class JpaConfiguration {

}
