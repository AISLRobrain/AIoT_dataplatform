package aisl.ksensor.servicemanager.servicemgmt.data.dao.impl;

import aisl.ksensor.servicemanager.servicemgmt.data.dao.ModelDAO;
import aisl.ksensor.servicemanager.servicemgmt.data.repository.ModelRepository;
import aisl.ksensor.servicemanager.servicemgmt.data.entity.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ModelDAOImpl implements ModelDAO {

    private final ModelRepository modelRepository;

    @Autowired
    public ModelDAOImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public Model insertModel(Model model) {
        return modelRepository.save(model);
    }

    @Override
    public Optional<Model> findModelById(Long id) {
        return modelRepository.findById(id);
    }

    @Override
    public Model findModelByModelName(String name) {
        return modelRepository.findByModelName(name);
    }

    @Override
    public Model updateModel(Model model) throws Exception {
        Optional<Model> selectedModelOptional = modelRepository.findById(model.getModelId());

        if (selectedModelOptional.isPresent()) {
            Model selectedmodel = selectedModelOptional.get();
            selectedmodel.setModelName(model.getModelName());
            selectedmodel.setIoTPlatformAEPath(model.getIoTPlatformAEPath());
            selectedmodel.setIotPlatformstateSubList(model.getIotPlatformstateSubList());
            selectedmodel.setIoTPlatformtargetSubList(model.getIoTPlatformtargetSubList());
            selectedmodel.setIoTPlatformtrainDataContainerList(model.getIoTPlatformtrainDataContainerList());
            selectedmodel.setIoTPlatformtestDataContainerList(model.getIoTPlatformtestDataContainerList());
            selectedmodel.setUpdateAt(LocalDateTime.now());
//            model.setUpdateBy(OPTIMIZATIONMANAGER);
//             todo.. OPTIMIZATIONMANAGER(optim manager config) should be initialized to updatedBy for prohibition of hard coding

            return modelRepository.save(selectedmodel);
        } else {
            throw new Exception("Model not found");
        }
    }


    @Override
    public void deleteModel(Long id) throws Exception {
        Optional<Model> selectedModelOptional = modelRepository.findById(id);

        if (selectedModelOptional.isPresent()) {
            Model model = selectedModelOptional.get();

            modelRepository.delete(model);
        } else {
            throw new Exception("Model not found");
        }

    }

}
