package aisl.ksensor.servicemanager.dataresource.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class DataModelDTO {
    private List<String> context;
    private String id;
    private String type;
    private String typeUri;
    private List<Attribute> attributes;

    @Data
    public static class Attribute {
        private String name;
        private String attributeType;
        private String attributeUri;
        private boolean hasObservedAt;
        private boolean hasUnitCode;
        private boolean isRequired;
        private String valueType;

        // 생성자, getter, setter 생략...
    }
}
