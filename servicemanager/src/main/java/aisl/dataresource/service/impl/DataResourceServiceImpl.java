package aisl.ksensor.servicemanager.dataresource.service.impl;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.DataResourceCode;
import aisl.ksensor.servicemanager.common.data.dto.ParameterRange;
import aisl.ksensor.servicemanager.common.transfer.http.service.HttpRequest;
import aisl.ksensor.servicemanager.dataresource.data.dto.*;
import aisl.ksensor.servicemanager.dataresource.service.DataResourceService;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.DataResourceSetupDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
public class DataResourceServiceImpl implements DataResourceService {

    @Value("${url.iotplatform.http}")
    private String IOTPLATFORMCSEBASEURL;

    @Value("${url.iotplatform.mqtt}")
    private String IOTPLATFORMMQTTURL;

    @Value("${url.iotdatahub.http}")
    private String IOTDATAHUBDATAMODELURL;

    @Autowired
    private HttpRequest httpRequest;

    public <T> Mono<DataResourceSetupDTO> provisioningDataResource(String serviceId, List<Map<String, Map<String, ParameterRange<T>>>> sensorParam, String sensorType) throws MalformedURLException, JsonProcessingException {
        DataResourceSetupDTO dataResourceSetupDTO = new DataResourceSetupDTO();

        HashMap<String, List<String>> resultMap = new HashMap<>();
        List<String> sensorList = new ArrayList<>();
        List<String> sensorContainerList = new ArrayList<>();
        List<String> trainDataContainerList = new ArrayList<>();
        List<String> testDataContainerList = new ArrayList<>();
        List<String> targetSubList = new ArrayList<>();
        List<String> stateSubList = new ArrayList<>();

        return postIoTPlatformResource(ServiceManagerCode.DataResourceCode.IOTPLATFORM_CSEBASE_ENDPOINT.getCode(), serviceId, null, ServiceManagerCode.DataResourceCode.AE.getCode())
                .flatMap(ioTPlatformAEPath -> {
                    dataResourceSetupDTO.setIoTPlatformAEPath(ioTPlatformAEPath);
                    return Mono.just(sensorParam);
                })
                .flatMapMany(Flux::fromIterable)
                .concatMap(sensorInfo -> Flux.fromIterable(sensorInfo.entrySet())
                        .concatMap(entry -> {
                            String sensorName = entry.getKey();
                            sensorList.add(sensorName);
                            Map<String, ParameterRange<T>> sensorLabel = entry.getValue();
                            try {
                                return postIoTPlatformResource(dataResourceSetupDTO.getIoTPlatformAEPath(), sensorName, sensorLabel, ServiceManagerCode.DataResourceCode.CNT.getCode())
                                        .flatMap(ioTPlatformSensorCNTPath -> {
                                            sensorContainerList.add(ioTPlatformSensorCNTPath);
                                            Mono<String> trainMono = Mono.just(ioTPlatformSensorCNTPath)
                                                    .flatMap(path -> {
                                                        try {
                                                            return postIoTPlatformResource(path, ServiceManagerCode.DataResourceCode.TRAINCNT.getCode(), null, ServiceManagerCode.DataResourceCode.CNT.getCode());
                                                        } catch (MalformedURLException | JsonProcessingException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    })
                                                    .doOnNext(trainDataContainerList::add);
                                            Mono<String> testMono = Mono.just(ioTPlatformSensorCNTPath)
                                                    .flatMap(path -> {
                                                        try {
                                                            return postIoTPlatformResource(path, ServiceManagerCode.DataResourceCode.TESTCNT.getCode(), null, ServiceManagerCode.DataResourceCode.CNT.getCode());
                                                        } catch (MalformedURLException | JsonProcessingException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    })
                                                    .doOnNext(testDataContainerList::add);
                                            Mono<String> targetMono = Mono.just(ioTPlatformSensorCNTPath)
                                                    .flatMap(path -> {
                                                        try {
                                                            return postIoTPlatformResource(path, DataResourceCode.TARGETCNT.getCode(), null, DataResourceCode.CNT.getCode());
                                                        } catch (MalformedURLException | JsonProcessingException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    })
                                                    .flatMap(targetPath -> {
                                                        try {
                                                            return postIoTPlatformResource(targetPath, serviceId + sensorName, null, ServiceManagerCode.DataResourceCode.SUB.getCode());
                                                        } catch (MalformedURLException | JsonProcessingException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    })
                                                    .doOnNext(targetSubList::add);
                                            Mono<String> stateMono = Mono.just(ioTPlatformSensorCNTPath)
                                                    .flatMap(path -> {
                                                        try {
                                                            return postIoTPlatformResource(path, ServiceManagerCode.DataResourceCode.STATECNT.getCode(), null, ServiceManagerCode.DataResourceCode.CNT.getCode());
                                                        } catch (MalformedURLException | JsonProcessingException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    })
                                                    .flatMap(statePath -> {
                                                        try {
                                                            return postIoTPlatformResource(statePath, serviceId + sensorName + DataResourceCode.STATESUB.getCode(), null, ServiceManagerCode.DataResourceCode.SUB.getCode());
                                                        } catch (MalformedURLException | JsonProcessingException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    })
                                                    .doOnNext(stateSubList::add);
                                            return Mono.zip(trainMono, testMono, targetMono, stateMono)
                                                    .doOnNext(tuple -> {
                                                        dataResourceSetupDTO.setSensorList(sensorList);
                                                        dataResourceSetupDTO.setIoTPlatformtargetSubList(targetSubList);
                                                        dataResourceSetupDTO.setIoTPlatformtrainDataContainerList(trainDataContainerList);
                                                        dataResourceSetupDTO.setIoTPlatformtestDataContainerList(testDataContainerList);
                                                        dataResourceSetupDTO.setIotPlatformstateSubList(stateSubList);
                                                    });

                                        });
                            } catch (MalformedURLException | JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                )
                .then(Mono.just(dataResourceSetupDTO));

    }

    public <T> Mono<String> postIoTPlatformResource(String previousPath, String resourceName, Object label, String resourceType) throws MalformedURLException, JsonProcessingException {
        URL iotPlatformURL = new URL(IOTPLATFORMCSEBASEURL + previousPath);
        String ty;
        ObjectMapper iotPlatformObjectMapper = new ObjectMapper();
        Object resourceDTO = null;
        switch (resourceType) {
            case "ae":
                ty = "2";
                IoTPlatformAEDTO.AE ae = new IoTPlatformAEDTO.AE();
                ae.setRn(resourceName);
                ae.setApi("");
                ae.setLbl(Collections.singletonList(resourceName));
                ae.setRr(true);
                ae.setPoa(Arrays.asList("http://203.250.148.120:9727"));
                IoTPlatformAEDTO aeDTO = new IoTPlatformAEDTO();
                aeDTO.setM2mAe(ae);
                resourceDTO = aeDTO;
                break;

            case "cnt":
                ty = "3";
                IoTPlatformCNTDTO.CNT cnt = new IoTPlatformCNTDTO.CNT();
                cnt.setRn(resourceName);
//                List<Map<String, List<Object>>> testArray = new ArrayList<>();
                try {
                    Map<String, Object> castedLabel = (Map<String, Object>) label;
//                    cnt.setLbl(Collections.singletonList(castedLabel));
                    cnt.setLbl(new ArrayList<>());
                } catch (ClassCastException e) {
                    cnt.setLbl(new ArrayList<>());
                }

//                cnt.setLbl((List<Map<String, List<Object>>>) label);
                cnt.setMbs(16384);
                IoTPlatformCNTDTO cntDTO = new IoTPlatformCNTDTO();
                cntDTO.setM2mCnt(cnt);
                resourceDTO = cntDTO;
                break;

            case "sub":
                ty = "23";
                IoTPlatformSUBDTO dto = new IoTPlatformSUBDTO();
                IoTPlatformSUBDTO.M2MSubDTO m2mSub = new IoTPlatformSUBDTO.M2MSubDTO();
                IoTPlatformSUBDTO.EncDTO enc = new IoTPlatformSUBDTO.EncDTO();
                m2mSub.setRn(resourceName);
                enc.setNet(List.of(3));
                m2mSub.setEnc(enc);
                m2mSub.setNu(List.of(IOTPLATFORMMQTTURL + '/' + resourceName + "?ct=json"));
                m2mSub.setExc(10);
                dto.setM2mSub(m2mSub);
                resourceDTO = dto;
                break;

            case "cin":
                ty = "4";
                break;

            default:
                log.error("Resource Type Error");
                return Mono.error(new RuntimeException("Resource Type Error"));
        }

        String ioTPlatformBody = iotPlatformObjectMapper.writeValueAsString(resourceDTO);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", DataResourceCode.ACCEPT_HEADERVALUE.getCode());
        headers.put("X-M2M-RI", DataResourceCode.X_M2M_RI_HEADERVALUE.getCode());
        headers.put("X-M2M-Origin", DataResourceCode.X_M2M_ORIGIN_HEADERVALUE.getCode());
        headers.put("Content-Type", DataResourceCode.CONTENT_TYPE_HEADERVALUE.getCode() + ty);

        return httpRequest.httpPostRequest(iotPlatformURL.toString(), ioTPlatformBody, headers)
                .thenReturn(previousPath + '/' + resourceName);
    }

    public HashMap<String, List<String>> servicingDataResource(String serviceId,
                                                               String sensorType,
                                                               List<String> trainDataContainerList,
                                                               List<String> testDataContainerList,
                                                               List<String> sensorList,
                                                               Map<String, Object> sensorParam) throws MalformedURLException, JsonProcessingException {

        HashMap<String, List<String>> servicingDataResource = new HashMap<>();

        String sensorParamId = generateUUID();
        String filterParamId = generateUUID();

        List<String> trainCntList = new ArrayList<>();
        for (String trainCnt : trainDataContainerList) {
            String result = postIoTPlatformResource(trainCnt, sensorParamId, sensorParam, ServiceManagerCode.DataResourceCode.CNT.getCode()).block();
            trainCntList.add(result);
        }
        servicingDataResource.put("trainDataPath", trainCntList);

        List<String> testCntList = new ArrayList<>();
        for (String testCnt : testDataContainerList) {
            String result = postIoTPlatformResource(testCnt, sensorParamId, sensorParam, ServiceManagerCode.DataResourceCode.CNT.getCode()).block();
            testCntList.add(result);
        }
        servicingDataResource.put("testDataPath", testCntList);

        List<String> rawTrainDatasetList = new ArrayList<>();
        for (String sensorId : sensorList) {
            String rawTrainDatasetName = serviceId + sensorId + DataResourceCode.TRAINSET.getCode() + sensorParamId;
            postDataset(rawTrainDatasetName, sensorType).block();
            postDataFlow(rawTrainDatasetName).block();
            rawTrainDatasetList.add(rawTrainDatasetName);
        }
        servicingDataResource.put("rawTrainDataPath", rawTrainDatasetList);

        List<String> rawTestDatasetList = new ArrayList<>();
        for (String sensorId : sensorList) {
            String rawTestDatasetName = serviceId + sensorId + DataResourceCode.TESTSET.getCode() + sensorParamId;
            postDataset(rawTestDatasetName, sensorType).block();
            postDataFlow(rawTestDatasetName).block();
            rawTestDatasetList.add(rawTestDatasetName);
        }
        servicingDataResource.put("rawTestDataPath", rawTestDatasetList);

        List<String> filterTrainDatasetList = new ArrayList<>();
        for (String sensorId : sensorList) {
            String filterTrainDatasetName = serviceId + sensorId + DataResourceCode.TRAINSET.getCode() + filterParamId;
            postDataset(filterTrainDatasetName, sensorType).block();
            postDataFlow(filterTrainDatasetName).block();
            filterTrainDatasetList.add(filterTrainDatasetName);
        }
        servicingDataResource.put("filterTrainDataPath", filterTrainDatasetList);

        List<String> filterTestDatasetList = new ArrayList<>();
        for (String sensorId : sensorList) {
            String filterTestDatasetName = serviceId + sensorId + DataResourceCode.TESTSET.getCode() + filterParamId;
            postDataset(filterTestDatasetName, sensorType).block();
            postDataFlow(filterTestDatasetName).block();
            filterTestDatasetList.add(filterTestDatasetName);
        }
        servicingDataResource.put("filterTestDataPath", filterTestDatasetList);

        return servicingDataResource;
    }



    public Mono<String> postDataset(String resourceName, String sensorType) throws JsonProcessingException, MalformedURLException {
        log.info("postdatasetlog");

        URL ioTDataSetURL = new URL(IOTDATAHUBDATAMODELURL + DataResourceCode.DATASET_ENDPOINT.getCode());

        ObjectMapper datasetObjectMapper = new ObjectMapper();

        DatasetDTO datasetDTO = new DatasetDTO();
        datasetDTO.setId(resourceName);
        datasetDTO.setName(resourceName);
        datasetDTO.setUpdateInterval("-");
        datasetDTO.setCategory("-");
        datasetDTO.setProviderOrganization("-");
        datasetDTO.setProviderSystem("-");
        datasetDTO.setIsProcessed("Source data");
        datasetDTO.setOwnership("-");
        datasetDTO.setLicense("CC BY");
        datasetDTO.setDatasetItems("-");
        datasetDTO.setTargetRegions("-");
        datasetDTO.setQualityCheckEnabled(true);
        datasetDTO.setDataModelId(sensorType);
        datasetDTO.setEnabled(true);

        String dataSetBody = datasetObjectMapper.writeValueAsString(datasetDTO);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return httpRequest.httpPostRequest(ioTDataSetURL.toString(), dataSetBody, headers)
                .thenReturn(resourceName);
    }

    public Mono<String> postDataFlow(String datasetId) throws JsonProcessingException, MalformedURLException {

        log.info("postdataflowlog");
        String endpoint = IOTDATAHUBDATAMODELURL + DataResourceCode.DATAFLOW_ENDPOINT.getCode();
        endpoint = String.format(endpoint, datasetId);

        ObjectMapper dataModelObjectMapper = new ObjectMapper();

        DataFlowDTO dataFlowDTO = new DataFlowDTO();
        dataFlowDTO.setDatasetId(datasetId);
        dataFlowDTO.setDescription("description");
        dataFlowDTO.setHistoryStoreType("full");
        dataFlowDTO.setEnabled(true);

        // 내부 클래스인 TargetTypeDTO 객체 생성 및 값 설정
        DataFlowDTO.TargetTypeDTO targetType = new DataFlowDTO.TargetTypeDTO();
        targetType.setType("dataServiceBroker");
        targetType.setBigDataStorageTypes(Arrays.asList("rdb"));

        // targetType 객체를 datasetFlow의 targetTypes 리스트에 추가
        dataFlowDTO.setTargetTypes(Arrays.asList(targetType));

        String dataflowBody = dataModelObjectMapper.writeValueAsString(dataFlowDTO);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return httpRequest.httpPostRequest(endpoint, dataflowBody, headers)
                .thenReturn(datasetId);

    }



    public String postDataModel(String resourceName) throws JsonProcessingException, MalformedURLException {

        URL ioTDataModelURL = new URL(IOTDATAHUBDATAMODELURL + DataResourceCode.DATAMODEL_ENDPOINT.getCode());

        ObjectMapper dataModelObjectMapper = new ObjectMapper();

        //            TODO Change Line whine hard coded
        DataModelDTO.Attribute attribute = new DataModelDTO.Attribute();
        attribute.setName("ParkingSpot");
        attribute.setAttributeType("Property");
        attribute.setAttributeUri("http://uri.citydatahub.kr/ngsi-ld/common/ParkingSpot");
        attribute.setHasObservedAt(false);
        attribute.setHasUnitCode(false);
        attribute.setRequired(false);
        attribute.setValueType("Integer");

        DataModelDTO dataModelDTO = new DataModelDTO();
        dataModelDTO.setContext(Arrays.asList("http://uri.citydatahub.kr/ngsi-ld/v1/context.jsonld"));
        dataModelDTO.setId(resourceName);
        dataModelDTO.setType("ParkingSpot");
        dataModelDTO.setTypeUri("http://uri.citydatahub.kr/ngsi-ld/parking/ParkingSpot");
        dataModelDTO.setAttributes(Arrays.asList(attribute));

        String dataModelBody = dataModelObjectMapper.writeValueAsString(dataModelDTO);
        WebClient webClient = WebClient.create();

        // Handle the response
        webClient.post()
                .uri(ioTDataModelURL.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dataModelBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(System.out::println, error -> {
                    // Handle the error
                    error.printStackTrace();
                });
        String newUrl = ioTDataModelURL + "/" + resourceName;


        return newUrl;
    }

    public String generateUUID() {
        UUID uuid = UUID.randomUUID();
        String shortUUID = uuid.toString().substring(0, 8);
        return shortUUID;
    }

}
