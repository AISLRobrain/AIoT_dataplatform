package aisl.ksensor.servicemanager.servicemgmt.data.repository;

import aisl.ksensor.servicemanager.servicemgmt.data.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Model findByModelName(String name);
}
