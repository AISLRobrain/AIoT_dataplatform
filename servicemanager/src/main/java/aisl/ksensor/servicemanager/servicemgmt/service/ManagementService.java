package aisl.ksensor.servicemanager.servicemgmt.service;

import aisl.ksensor.servicemanager.common.data.dto.OptimizationParameters;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.ServiceSetupRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;

public interface ManagementService {

    public Mono<Void> setupService(ServiceSetupRequestDTO serviceRequestDTO)  throws MalformedURLException, JsonProcessingException;

    public boolean runService(String modelName, String engineType) throws Exception;

    public void deleteService(Long serviceId);

    public void updateOptimizationParameters(OptimizationParameters optimizationDTO) throws Exception;
}
