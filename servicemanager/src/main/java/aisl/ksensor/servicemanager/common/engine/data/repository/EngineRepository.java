package aisl.ksensor.servicemanager.common.engine.data.repository;

import aisl.ksensor.servicemanager.common.engine.data.entity.Engine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineRepository extends JpaRepository<Engine, Long> {
    Engine findByEngineContainerName(String id);
}
