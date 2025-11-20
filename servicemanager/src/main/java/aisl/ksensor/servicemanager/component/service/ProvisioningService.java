package aisl.ksensor.servicemanager.component.service;

import aisl.ksensor.servicemanager.component.data.dto.PropagationSetupRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;

public interface ProvisioningService {

    public Mono<String> setupModel(String manager, PropagationSetupRequestDTO provisioningSetupRequestDTO) throws JsonProcessingException, MalformedURLException;
}
