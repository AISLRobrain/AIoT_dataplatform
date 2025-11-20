package aisl.ksensor.servicemanager.component.service.impl;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ServiceCommonCode;
import aisl.ksensor.servicemanager.common.transfer.http.service.HttpRequest;
import aisl.ksensor.servicemanager.component.data.dto.PropagationSetupRequestDTO;
import aisl.ksensor.servicemanager.component.service.ProvisioningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
public class ProvisioningServiceImpl implements ProvisioningService {

    @Value("${url.optimizationmanager.http}")
    private String OPTIMIZATIONMANAGERURL;

    @Value("${url.mlmanager.http}")
    private String MLMANAGERURL;

    @Value("${url.filtermanager.http}")
    private String FILTERMANAGERURL;

    @Autowired
    private HttpRequest httpRequest;

    public Mono<String> setupModel(String manager, PropagationSetupRequestDTO propagationSetupRequestDTO) throws JsonProcessingException, MalformedURLException {
        URL requestURL;
        switch (manager) {
            case "optimizationmanager":
                requestURL = new URL(OPTIMIZATIONMANAGERURL + ServiceCommonCode.SETUP_ENDPOINT.getCode());
                break;

            case "mlmanager":
                requestURL = new URL(MLMANAGERURL + ServiceCommonCode.SETUP_ENDPOINT.getCode());
                break;

            case "filtermanager":
                requestURL = new URL(FILTERMANAGERURL + ServiceCommonCode.SETUP_ENDPOINT.getCode());
                break;
            default:
                log.error("Manager Type Error");
                return Mono.error(new RuntimeException("Manager Type Error"));
        }

        ObjectMapper requestObjectMapper = new ObjectMapper();
        String requestBody = requestObjectMapper.writeValueAsString(propagationSetupRequestDTO);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return httpRequest.httpPostRequest(requestURL.toString(), requestBody, headers);
    }
}
