package aisl.ksensor.servicemanager.servicemgmt.data.entity;

import aisl.ksensor.servicemanager.common.code.JsonConverter;
import aisl.ksensor.servicemanager.common.data.dto.ParameterRange;
import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "model", schema = "service_manager")
@Data
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "modelId")
    private Long modelId;

    @Column(name = "modelName", nullable = false)
    private String modelName;

    @Column(name = "sensorType", nullable = false)
    private String sensorType;

    @Column(name = "sensorList", nullable = false)
    @ElementCollection
    private List<String> sensorList;

    @Column(name = "ioTPlatformAEPath", nullable = false)
    private String ioTPlatformAEPath;

    @Column(name = "iotPlatformstateSubList", nullable = false)
    @ElementCollection
    private List<String> iotPlatformstateSubList;

    @Column(name = "ioTPlatformtargetSubList", nullable = false)
    @ElementCollection
    private List<String> ioTPlatformtargetSubList;

    @Column(name = "ioTPlatformtrainDataContainerList", nullable = false)
    @ElementCollection
    private List<String> ioTPlatformtrainDataContainerList;

    @Column(name = "ioTPlatformtestDataContainerList", nullable = false)
    @ElementCollection
    private List<String> ioTPlatformtestDataContainerList;

    @Column(name = "sensing_stop_condition", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> sensingStopCondition;

    @Column(name = "sensorParamRange", columnDefinition = "TEXT")
    @ElementCollection
    @Convert(converter = JsonConverter.class)
    private List<Map<String, Object>> sensorParamRange;

    @Column(name = "filterParamRange", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> filterParamRange;

    @Column(name = "mlParamRange", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> mlParamRange;

    @Column(name = "optimizationParamRange", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> optimizationParamRange;

    @Column(name = "sensorTargets", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, String> sensorTargets;

    @Column(name = "createAt", updatable = false)
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @Column(name = "createBy", nullable = false)
    private String createBy;

    @Column(name = "updateBy")
    private String updateBy;

    @Column(name = "optIter")
    private Integer optIter;

    public void setSensingStopCondition(SensingStopCondition sensingStopCondition) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map result = objectMapper.convertValue(sensingStopCondition, Map.class);
        this.sensingStopCondition = result;
    }

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
