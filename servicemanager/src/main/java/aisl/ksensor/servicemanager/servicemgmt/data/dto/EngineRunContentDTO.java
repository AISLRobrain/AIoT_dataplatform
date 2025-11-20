package aisl.ksensor.servicemanager.servicemgmt.data.dto;

import aisl.ksensor.servicemanager.common.data.dto.DataResourceInformation;
import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class EngineRunContentDTO {
    private Map<String, Object> sensorParam;
    private Map<String, Object> filterParam;
    private Map<String, Object> hyperParam;
    private String sensorType;
    private Map<String, String> sensorTargets;
    private DataResourceInformation dataResourceInformation;

    private SensingStopCondition sensingStopCondition;

    public void setDataResourceInformation(Map<String, Object> dataResourceInformation) {
        ObjectMapper objectMapper = new ObjectMapper();
        DataResourceInformation result = objectMapper.convertValue(dataResourceInformation, DataResourceInformation.class);
        this.dataResourceInformation = result;
    }

    public void setSensingStopCondition(Map<String, Object> sensingStopCondition) {
        ObjectMapper objectMapper = new ObjectMapper();
        SensingStopCondition result = objectMapper.convertValue(sensingStopCondition, SensingStopCondition.class);
        this.sensingStopCondition = result;
    }
}
