package aisl.ksensor.servicemanager.dataresource.data.dto;

import lombok.Data;
import java.util.List;

@Data
public class DataFlowDTO {

    private String datasetId;
    private String description;
    private String historyStoreType;
    private Boolean enabled;
    private List<TargetTypeDTO> targetTypes;

    @Data
    public static class TargetTypeDTO {
        private String type;
        private List<String> bigDataStorageTypes;
    }
}