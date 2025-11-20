package aisl.ksensor.servicemanager.dataresource.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IoTPlatformSUBDTO {
    @JsonProperty("m2m:sub")
    private M2MSubDTO m2mSub;

    @Data
    public static class M2MSubDTO {
        private String rn;
        private EncDTO enc;
        private List<String> nu;
        private int exc;
    }

    @Data
    public static class EncDTO {
        private List<Integer> net;
    }
}
