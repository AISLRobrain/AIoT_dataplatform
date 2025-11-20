package aisl.ksensor.servicemanager.common.engine.data.dao;

import aisl.ksensor.servicemanager.common.engine.data.entity.Engine;

import java.util.Optional;

public interface EngineDAO {

    Engine insertEngine(Engine engine);

    Optional<Engine> findEngineById(Long id);

    Engine findEngineByEngineContainerID(String id);

    Engine updateEngine(Engine engine) throws Exception;

    void deleteEngine(Long id) throws Exception;
}
