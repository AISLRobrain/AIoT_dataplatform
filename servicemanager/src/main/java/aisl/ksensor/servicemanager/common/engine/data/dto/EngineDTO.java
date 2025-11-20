package aisl.ksensor.servicemanager.common.engine.data.dto;

import lombok.Data;

@Data
public class EngineDTO {
    private String engineId; //    lately set
    private String engineContainerName;
    private String engineType;
    private String imageId; //    lately set
    private String engineNetworkName;
//    private String brokerTopicName; //    last_lately set
//    private String brokerConsumerName; //    last_lately set
    private String openPort;
    private String allocatedCpuCores;
    private Long allocatedMemory;
    private Integer allocatedGpuCounts;
    private String allocatedGpuMemory;
    private String accessToken; //    lately set
    private String adminName;
    private String adminPassword;
    private Integer environmentID;
    private String sensorType;
//    private Long optimizationId; //    late_lately set
    private String createBy;
}
