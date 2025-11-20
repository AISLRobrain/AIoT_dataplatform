package aisl.ksensor.servicemanager.common.engine.data.dao.impl;

import aisl.ksensor.servicemanager.common.engine.data.dao.EngineDAO;
import aisl.ksensor.servicemanager.common.engine.data.entity.Engine;
import aisl.ksensor.servicemanager.common.engine.data.repository.EngineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EngineDAOImpl implements EngineDAO {

    private final EngineRepository engineRepository;

    @Autowired
    public EngineDAOImpl(EngineRepository engineRepository) {
        this.engineRepository = engineRepository;
    }
    @Override
    public Engine insertEngine(Engine engine) {
        return engineRepository.save(engine);
    }

    @Override
    public Optional<Engine> findEngineById(Long id) {
        return engineRepository.findById(id);
    }
    @Override
    public Engine findEngineByEngineContainerID(String id) {
        return engineRepository.findByEngineContainerName(id);
    }

    @Override
    public Engine updateEngine(Engine engine) throws Exception {
        Optional<Engine> selectedEngineOptional = engineRepository.findById(engine.getEngineId());

        if (selectedEngineOptional.isPresent()) {
            Engine selectedEngine = selectedEngineOptional.get();
            selectedEngine.setEngineContainerName(engine.getEngineContainerName());
            selectedEngine.setEngineType(engine.getEngineType());
            selectedEngine.setUpdateAt(LocalDateTime.now());
            selectedEngine.setUpdateBy(engine.getUpdateBy());

            return engineRepository.save(selectedEngine);
        } else {
            throw new Exception("Engine not found");
        }
    }

    @Override
    public void deleteEngine(Long id) throws Exception {
        Optional<Engine> selectedEngineOptional = engineRepository.findById(id);

        if (selectedEngineOptional.isPresent()) {
            Engine engine = selectedEngineOptional.get();

            engineRepository.delete(engine);
        } else {
            throw new Exception("Engine not found");
        }
    }

}
