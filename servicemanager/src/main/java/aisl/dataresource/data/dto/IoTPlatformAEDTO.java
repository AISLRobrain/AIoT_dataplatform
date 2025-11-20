package aisl.ksensor.servicemanager.dataresource.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IoTPlatformAEDTO {
    @JsonProperty("m2m:ae")
    private AE m2mAe;

    @Data
    public static class AE {
        private String rn;
        private String api;
        private List<String> lbl;
        private boolean rr;
        private List<String> poa;

    }
}