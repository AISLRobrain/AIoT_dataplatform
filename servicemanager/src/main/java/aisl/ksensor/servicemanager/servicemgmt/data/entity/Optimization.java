package aisl.ksensor.servicemanager.servicemgmt.data.entity;

import aisl.ksensor.servicemanager.common.code.JsonConverter;
import aisl.ksensor.servicemanager.common.data.dto.DataResourceInformation;
import aisl.ksensor.servicemanager.common.data.dto.SensingStopCondition;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "optimization")
@Data
public class Optimization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "service_id")
    private String serviceId;

    @ElementCollection
    @Column(name = "sensor_param", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> sensorParam;

    @Column(name = "filter_param", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> filterParam;

    @Column(name = "hyper_param", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> hyperParam;

    @Column(name = "opt_iter")
    private Integer optIter;

//    @Column(name = "sensor_param_id")
//    private String sensorParamId;
//
//    @Column(name = "filter_param_id")
//    private String filterParamId;

    @Column(name = "data_resource_information", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> dataResourceInformation;

    public void setDataResourceInformation(DataResourceInformation dataResourceInformation) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map result = objectMapper.convertValue(dataResourceInformation, Map.class);
        this.dataResourceInformation = result;
    }



//    @PrePersist
//    public void initializeUUIDs() {
//        this.sensorParamId = generateShortUUID();
//        this.filterParamId = generateShortUUID();
//    }

//    private String generateShortUUID() {
//        return UUID.randomUUID().toString().substring(0, 8);
//    }


}

