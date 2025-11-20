package aisl.ksensor.servicemanager.common.data.dto;

import lombok.Data;

@Data
public class SensingStopCondition {
    private String stopCondition;
    private Integer stopConditionValue;
}