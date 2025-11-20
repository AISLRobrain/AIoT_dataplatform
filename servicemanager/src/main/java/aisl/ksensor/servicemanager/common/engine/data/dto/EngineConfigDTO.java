package aisl.ksensor.servicemanager.common.engine.data.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EngineConfigDTO {
    private String name;
    private String image;
    private List<String> Env;
    private NetworkingConfig networkingConfig;
    private Map<String, Object> exposedPorts;
    private HostConfig hostConfig;

    @Data
    public static class NetworkingConfig {
        private Map<String, Object> endpointsConfig;
    }

    @Data
    public static class HostConfig {
        private List<String> binds;
        private Map<String, List<Object>> portBindings;
        private Resources resources;
    }

    @Data
    public static class Resources {
        private String cpusetCpus;
        private long memory;
        private List<DeviceRequest> deviceRequests;
    }

    @Data
    public static class DeviceRequest {
        private String driver;
        private int count;
        private List<String> deviceIDs;
        private List<List<String>> capabilities;
        private Options options;
    }

    @Data
    public static class Options {
        private String comNvidiaLimit;
    }
}
