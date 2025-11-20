package aisl.ksensor.servicemanager.common.data.dto;

import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OptimizationParameters {

    private String serviceId;
    private Map<String, Object> sensorParam;
    private Map<String, Object> filterParam;
    private Map<String, Object> hyperParam;
}
