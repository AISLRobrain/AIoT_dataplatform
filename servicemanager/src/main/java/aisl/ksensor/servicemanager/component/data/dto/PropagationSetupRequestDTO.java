package aisl.ksensor.servicemanager.component.data.dto;

import aisl.ksensor.servicemanager.common.data.dto.ParameterRange;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.ServiceSetupRequestDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PropagationSetupRequestDTO<T> {

    private Long modelId;
    private String serviceId;
    private String optimizationAlgorithmType;
    private Map<String, Object> optimizationParam;
    private List<Map<String, Map<String, ParameterRange<T>>>> sensorParam;
    private Map<String, Map<String, ParameterRange<T>>> filterParam;
    private Map<String, Map<String, ParameterRange<T>>> hyperParam;
    private String sensorType;



    public PropagationSetupRequestDTO(ServiceSetupRequestDTO serviceSetupRequestDTO) {
        this.serviceId = serviceSetupRequestDTO.getServiceId();
        this.optimizationAlgorithmType = serviceSetupRequestDTO.getOptimizationAlgorithmType();
        this.optimizationParam = serviceSetupRequestDTO.getOptimizationParam();
        this.sensorParam = serviceSetupRequestDTO.getSensorParam();
        this.filterParam = serviceSetupRequestDTO.getFilterParam();
        this.hyperParam = serviceSetupRequestDTO.getHyperParam();
        this.sensorType = serviceSetupRequestDTO.getSensorType();
    }
}

