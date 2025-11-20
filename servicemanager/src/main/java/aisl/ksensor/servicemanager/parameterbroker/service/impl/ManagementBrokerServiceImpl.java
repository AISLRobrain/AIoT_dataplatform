package aisl.ksensor.servicemanager.parameterbroker.service.impl;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ParameterBrokerCode;

import aisl.ksensor.servicemanager.common.transfer.kafka.service.KafkaProducerService;
import aisl.ksensor.servicemanager.dataresource.data.dto.IoTPlatformAEDTO;
import aisl.ksensor.servicemanager.dataresource.data.dto.IoTPlatformCNTDTO;
import aisl.ksensor.servicemanager.dataresource.data.dto.IoTPlatformSUBDTO;
import aisl.ksensor.servicemanager.parameterbroker.data.dto.DefaultBrokerMassageDTO;
import aisl.ksensor.servicemanager.parameterbroker.service.ManagementBrokerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class ManagementBrokerServiceImpl implements ManagementBrokerService {

    private final KafkaProducerService kafkaProducerService;

    private final ObjectMapper objectMapper;

    @Autowired
    public ManagementBrokerServiceImpl(KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = new ObjectMapper();
    }

    public <T> boolean serviceSetup(String serviceId, T content) {
        try {
            String topic = ParameterBrokerCode.OPTIMIZATION_ENGINE_HISTORY_QUEUE_NOTIFICATION_TOPIC.getCode();
            String key = serviceId;

            DefaultBrokerMassageDTO setupMessage = new DefaultBrokerMassageDTO(ParameterBrokerCode.SETUP_MESSAGE.getCode(), content);

            // DTO를 JSON 문자열로 변환
            String message = objectMapper.writeValueAsString(setupMessage);

            kafkaProducerService.sendMessage(topic, key, message);

            // 메시지 전송 후의 로직 (예: 성공 여부 반환)
            return true;
        }
        catch(Exception e){
            // 메시지 전송 후의 로직 (예: 성공 여부 반환)
            return false;
        }

    }

    public <T> boolean engineRun(String serviceId, Integer optIter, String engineType, T content) {

        String key = serviceId;

        String topic = null;
        String toEngineType = null;

        switch (engineType) {
            case "optimization-engine":
                topic = ParameterBrokerCode.OPTIMIZATION_ENGINE_REQUEST_NOTIFICATION_TOPIC.getCode();
                toEngineType = ServiceManagerCode.ServiceZoneCode.OPTIMIZATION_ENGINE.getCode();
                break;

            case "ml-engine":
                topic = ParameterBrokerCode.ML_ENGINE_REQUEST_NOTIFICATION_TOPIC.getCode();
                toEngineType = ServiceManagerCode.ServiceZoneCode.ML_ENGINE.getCode();
                break;

            case "filter-engine":
                topic = ParameterBrokerCode.FILTER_ENGINE_REQUEST_NOTIFICATION_TOPIC.getCode();
                toEngineType = ServiceManagerCode.ServiceZoneCode.FILTER_ENGINE.getCode();
                break;

            case "sensor-controller":
                topic = ParameterBrokerCode.SENSOR_CONTROLLER_REQUEST_NOTIFICATION_TOPIC.getCode();
                toEngineType = ServiceManagerCode.ServiceZoneCode.SENSOR_CONTROLLER.getCode();
                break;

            default:
                log.error(engineType + " is not a valid service type.");
                return false;
        }

        try {
            DefaultBrokerMassageDTO runMessage = new DefaultBrokerMassageDTO(ParameterBrokerCode.RUN_MESSAGE.getCode(), content);

            runMessage.setEventId(UUID.randomUUID().toString());
            runMessage.setEventTransId(generateUUIDFromNumber(optIter).toString());
            runMessage.setContentType(ServiceManagerCode.ServiceCommonCode.CONTENT_TYPE.getCode());
            runMessage.setMessageType(toEngineType);
            runMessage.setFromSystemName(ServiceManagerCode.ServiceCommonCode.MANAGER.getCode());
            runMessage.setToSystemName(toEngineType);

            // DTO를 JSON 문자열로 변환
            String message = objectMapper.writeValueAsString(runMessage);
            System.out.println("topic is");
            System.out.println(topic);
            kafkaProducerService.sendMessage(topic, key, message);

            // 메시지 전송 후의 로직 (예: 성공 여부 반환)
            return true;
        }
        catch(Exception e){
            // 메시지 전송 후의 로직 (예: 성공 여부 반환)
            return false;
        }
    }

    public static UUID generateUUIDFromNumber(int number) {
        byte[] bytes = Integer.toString(number).getBytes();
        return UUID.nameUUIDFromBytes(bytes);
    }
}
