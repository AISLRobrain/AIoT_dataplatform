package aisl.ksensor.servicemanager.servicemgmt.data.dao;

import aisl.ksensor.servicemanager.servicemgmt.data.entity.Model;

import java.util.Optional;
public interface ModelDAO {

    Model insertModel(Model model);

    Optional<Model> findModelById(Long id);

    Model findModelByModelName(String name);

    Model updateModel(Model model) throws Exception;

    void deleteModel(Long id) throws Exception;
}
