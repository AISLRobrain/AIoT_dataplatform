package aisl.ksensor.servicemanager.servicemgmt.data.dto;


import aisl.ksensor.servicemanager.common.data.dto.ParameterRange;
import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import lombok.Data;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ServiceSetupRequestDTO", description = "서비스 등록 및 프로비저닝 요청 DTO")
@Data
public class ServiceSetupRequestDTO<T> {

    private String serviceId;
    private String callId;
    private String optimizationAlgorithmType;
    private Map<String, String> sensorTargets;
    private Map<String, Object> optimizationParam;
    private List<Map<String, Map<String, ParameterRange<T>>>> sensorParam;
    private Map<String, Map<String, ParameterRange<T>>> filterParam;
    private Map<String, Map<String, ParameterRange<T>>> hyperParam;
    private SensingStopCondition sensingStopCondition;
    private String sensorType;

}

