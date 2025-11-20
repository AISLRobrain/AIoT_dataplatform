package aisl.ksensor.servicemanager.dataresource.service;

import aisl.ksensor.servicemanager.common.data.dto.ParameterRange;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.DataResourceSetupDTO;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.ServiceSetupRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DataResourceService {

    public <T> Mono<String> postIoTPlatformResource(String previousPath, String resourceName, Object label, String resourceType) throws MalformedURLException, JsonProcessingException;

    public <T> Mono<DataResourceSetupDTO> provisioningDataResource(String serviceId, List<Map<String, Map<String, ParameterRange<T>>>> sensorParam, String sensorType) throws MalformedURLException, JsonProcessingException;

    public HashMap<String, List<String>> servicingDataResource(String serviceId,
                                                               String sensorType,
                                                               List<String> trainDataContainerList,
                                                               List<String> testDataContainerListm,
                                                               List<String> sensorList,
                                                               Map<String, Object> sensorParam) throws MalformedURLException, JsonProcessingException;

    public Mono<String> postDataset(String resourceName, String sensorType) throws JsonProcessingException, MalformedURLException;

    public Mono<String> postDataFlow(String datasetId) throws JsonProcessingException, MalformedURLException;
    public String postDataModel(String resourceName) throws JsonProcessingException, MalformedURLException;
}
