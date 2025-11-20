package aisl.ksensor.servicemanager.servicemgmt.data.dto;

import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import lombok.Data;

import java.util.List;

@Data
public class DataResourceSetupDTO {

    private String ioTPlatformAEPath;
    private List<String> iotPlatformstateSubList;
    private List<String> ioTPlatformtargetSubList;
    private List<String> ioTPlatformtrainDataContainerList;
    private List<String> ioTPlatformtestDataContainerList;
    private List<String> sensorList;
}
