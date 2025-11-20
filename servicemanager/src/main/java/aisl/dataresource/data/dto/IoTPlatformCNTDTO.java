package aisl.ksensor.servicemanager.dataresource.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IoTPlatformCNTDTO {
    @JsonProperty("m2m:cnt")
    private CNT m2mCnt;

    @Data
    public static class CNT {
        private String rn;
        private List<Map<String, Object>> lbl;
//        private List<Map<String, Object>> lbl;
        private int mbs;
    }
}
