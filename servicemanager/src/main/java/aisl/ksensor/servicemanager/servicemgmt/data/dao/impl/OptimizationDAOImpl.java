package aisl.ksensor.servicemanager.servicemgmt.data.dao.impl;

import aisl.ksensor.servicemanager.servicemgmt.data.dao.OptimizationDAO;
import aisl.ksensor.servicemanager.servicemgmt.data.entity.Optimization;
import aisl.ksensor.servicemanager.servicemgmt.data.repository.OptimizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class OptimizationDAOImpl implements OptimizationDAO {

    private final OptimizationRepository optimizationRepository;

    @Autowired
    public OptimizationDAOImpl(OptimizationRepository optimizationRepository) {
        this.optimizationRepository = optimizationRepository;
    }

    @Override
    public Optimization insertOptimization(Optimization optimization) {
        return optimizationRepository.save(optimization);
    }

    @Override
    public void deleteOptimizationsByServiceId(String serviceId) {
        optimizationRepository.deleteByServiceId(serviceId);
    }

    @Override
    public Optimization findLastOptimizationByServiceId(String serviceId) {
        return optimizationRepository.findTopByServiceIdOrderByOptIterDesc(serviceId).orElse(null);
    }


}
