package aisl.ksensor.servicemanager.servicemgmt.data.dto;

import lombok.Data;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(name = "WorkerResponseNotificationDTO", description = "워커 응답 알림 DTO")
public class WorkerResponseNotificationDTO {

    @Schema(name = "eventId", description = "이벤트 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID eventId;

    @Schema(name = "eventTransId", description = "이벤트 전송 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
    private UUID eventTransId;

    @Schema(name = "fromSystemName", description = "fromSystemName", example = "SensorInstance or Filter or ML or Optimization Engine")
    private String fromSystemName;

    @Schema(name = "toSystemName", description = "toSystemName", example = "EAI")
    private String toSystemName;

    @Schema(name = "contentType", description = "콘텐츠 유형", example = "application/json")
    private String contentType;

    @Schema(name = "content", description = "콘텐츠")
    private Content content;

    @Data
    public static class Content {

        @Schema(name = "serviceId", description = "서비스 ID", example = "aislOptimization")
        private String serviceId;

        @Schema(name = "engineType", description = "엔진 유형", example = "DataSensing or Filtering or ML or Optimization")
        private String engineType;
    }
}
