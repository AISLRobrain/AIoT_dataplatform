package aisl.ksensor.servicemanager.common.data.dto;

import aisl.ksensor.servicemanager.common.data.dto.OptimizationParameters;
import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import lombok.Data;

import java.util.List;

@Data
public class DataResourceInformation {

    IoTPlatformInformation ioTPlatformInformation;
    DataHubInformation dataHubInformation;

    @Data
    public static class IoTPlatformInformation {
        List<String> statePath;
        List<String> targetPath;
        List<String> trainDataPath;
        List<String> testDataPath;
    }

    @Data
    public static class DataHubInformation {
        List<String> rawTrainDataPath;
        List<String> rawTestDataPath;
        List<String> filterTrainDataPath;
        List<String> filterTestDataPath;
    }

}
