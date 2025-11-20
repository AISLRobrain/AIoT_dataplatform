package aisl.ksensor.servicemanager.dataresource.data.dto;

import lombok.Data;

@Data
public class DatasetDTO {
    private String id;
    private String name;
    private String updateInterval;
    private String category;
    private String providerOrganization;
    private String providerSystem;
    private String isProcessed;
    private String ownership;
    private String license;
    private String datasetItems;
    private String targetRegions;
    private boolean qualityCheckEnabled;
    private String dataModelId;
    private boolean enabled;
}

