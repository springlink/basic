package sourcefx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import sourcefx.BasicApplication;
import sourcefx.core.AppJpaRepository;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = BasicApplication.class, repositoryBaseClass = AppJpaRepository.class)
public class JpaConfiguration {

}
