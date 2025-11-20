package aisl.ksensor.servicemanager.servicemgmt.data.repository;


import aisl.ksensor.servicemanager.servicemgmt.data.entity.Optimization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptimizationRepository extends JpaRepository<Optimization, Long> {
    Optional<Optimization> findTopByServiceIdOrderByOptIterDesc(String serviceId);
    void deleteByServiceId(String serviceId);
}