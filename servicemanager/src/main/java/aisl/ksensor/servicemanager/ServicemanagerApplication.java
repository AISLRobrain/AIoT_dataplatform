package aisl.ksensor.servicemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"aisl.ksensor.servicemanager.servicemgmt.data.repository", "aisl.ksensor.servicemanager.common.engine.data.repository"})
@ComponentScan(basePackages = {"aisl.ksensor.servicemanager.servicemgmt", "aisl.ksensor.servicemanager.common", "aisl.ksensor.servicemanager.dataresource", "aisl.ksensor.servicemanager.parameterbroker", "aisl.ksensor.servicemanager.component", "aisl.ksensor.servicemanager.common.engine"})
@EntityScan(basePackages = { "aisl.ksensor.servicemanager.servicemgmt.data.entity", "aisl.ksensor.servicemanager.common.engine.data.entity"})
@SpringBootApplication
public class ServicemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicemanagerApplication.class, args);
	}

}
