package aisl.ksensor.servicemanager.servicemgmt.data.dao;

import aisl.ksensor.servicemanager.servicemgmt.data.entity.Optimization;

import java.util.List;
import java.util.Optional;

public interface OptimizationDAO {
    Optimization insertOptimization(Optimization Optimization);
    public void deleteOptimizationsByServiceId(String serviceId);
    public Optimization findLastOptimizationByServiceId(String serviceId);
}
