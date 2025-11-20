package aisl.ksensor.servicemanager.servicemgmt.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "ServiceRunRequestDTO", description = "서비스 실행 요청 DTO")
@Data
public class EAIEngineRunRequestDTO {
    @Schema(name = "serviceId", description = "서비스 ID(=모델Name)", example = "aislOptimization")
    private String serviceId;

}
