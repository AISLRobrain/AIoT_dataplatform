package aisl.ksensor.servicemanager.common.engine.data.entity;

import aisl.ksensor.servicemanager.common.code.JsonConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "sensor_controller", schema = "service_manager")
@Data
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "engineId")
    private Long engineId;

    @Column(name = "engineContainerId", nullable = false)
    private String engineContainerId;

    @Column(name = "engineContainerName", nullable = false)
    private String engineContainerName;

    @Column(name = "engineType", nullable = false)
    private String engineType;

    @Column(name = "engineNetworkName", nullable = false)
    private String engineNetworkName;

//    @Column(name = "BrokerTopicName", nullable = false)
//    private String BrokerTopicName;
//
//    @Column(name = "BrokerConsumerName", nullable = false)
//    private String BrokerConsumerName;

    @Column(name = "openPort", nullable = false)
    private String openPort;

    @Convert(converter = JsonConverter.class)
    @Column(name = "allocatedResources")
    private Map<String, Object> allocatedResources;

    @Column(name = "AccessToken", nullable = false)
    private String AccessToken;

    @Column(name = "AdminName", nullable = false)
    private String AdminName;

    @Column(name = "EnvironmentID", nullable = false)
    private Integer EnvironmentID;

//    @OneToOne
//    @JoinColumn(name = "OptimizationId", referencedColumnName = "OptimizationId")
//    private Optimization optimization;

    @Column(name = "createAt", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "createBy", nullable = false)
    private String createBy;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    @Column(name = "updateBy")
    private String updateBy;

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }
}
