package aisl.ksensor.servicemanager.servicemgmt.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "SensorControllerRunDTO", description = "센서 컨트롤러 실행 DTO")
public class SensorControllerRunDTO {

    @Schema(name = "serviceId", description = "서비스 ID(=모델Name)", example = "aislOptimization")
    private String serviceId;

    @Schema(name = "sensorKey", description = "센서 jwtKey", example = "{\"radar1\": {\"jwtKey\": \"jdshfbhjdsbfhjsdbfhjDGRGDRGHDGFDFGXFG\"}}")
    private Map<String, jwt> sensorKey;

    public static class jwt {
        @Schema(name = "jwtKey", description = "센서 jwtKey", example = "jdshfbhjdsbfhjsdbfhjDGRGDRGHDGFDFGXFG")
        private String jwtKey;
    }
}
