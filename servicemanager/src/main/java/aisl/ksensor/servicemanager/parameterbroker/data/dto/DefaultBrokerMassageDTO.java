package aisl.ksensor.servicemanager.parameterbroker.data.dto;

import lombok.Data;

@Data
public class DefaultBrokerMassageDTO {

    String eventId;
    String eventTransId;
    String fromSystemName;
    String toSystemName;
    String contentType;
    String messageType;
    Object content;

    public DefaultBrokerMassageDTO(String messageType, Object content) {
        this.messageType = messageType;
        this.content = content;
    }
}
