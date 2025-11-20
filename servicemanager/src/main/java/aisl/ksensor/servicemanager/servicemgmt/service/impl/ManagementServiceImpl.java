package aisl.ksensor.servicemanager.servicemgmt.service.impl;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ServiceZoneCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ServiceCommonCode;
import aisl.ksensor.servicemanager.common.data.dto.DataResourceInformation;
import aisl.ksensor.servicemanager.common.data.dto.OptimizationParameters;
import aisl.ksensor.servicemanager.common.engine.service.EngineService;
import aisl.ksensor.servicemanager.component.data.dto.PropagationSetupRequestDTO;
import aisl.ksensor.servicemanager.component.service.ProvisioningService;
import aisl.ksensor.servicemanager.dataresource.service.DataResourceService;
import aisl.ksensor.servicemanager.parameterbroker.service.ManagementBrokerService;
import aisl.ksensor.servicemanager.servicemgmt.data.dao.ModelDAO;
import aisl.ksensor.servicemanager.servicemgmt.data.dao.OptimizationDAO;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.*;
import aisl.ksensor.servicemanager.servicemgmt.data.entity.Model;
import aisl.ksensor.servicemanager.servicemgmt.data.entity.Optimization;
import aisl.ksensor.servicemanager.servicemgmt.service.ManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ManagementServiceImpl implements ManagementService {

    private final ModelDAO modelDAO;

    private final OptimizationDAO optimizationDAO;

    @Autowired
    public ManagementServiceImpl(ModelDAO modelDAO, OptimizationDAO optimizationDAO) {
        this.modelDAO = modelDAO;
        this.optimizationDAO = optimizationDAO;
    }

    @Autowired
    private ProvisioningService provisioningService;

    @Autowired
    private DataResourceService dataResourceService;

    @Autowired
    private ManagementBrokerService managementBrokerService;

    @Autowired
    private EngineService engineService;


    public Mono<Void> setupService(ServiceSetupRequestDTO serviceRequestDTO) throws MalformedURLException, JsonProcessingException {

        String modelName = serviceRequestDTO.getServiceId();

        Model model = new Model();


        return dataResourceService.provisioningDataResource(modelName, serviceRequestDTO.getSensorParam(), serviceRequestDTO.getSensorType())
                .flatMap(setupDataResourceResult -> {

                    List<String> sensorStoragePath = new ArrayList<>();

                    DataResourceSetupDTO dataResourceSetupDTO = (DataResourceSetupDTO) setupDataResourceResult;

                    System.out.println(dataResourceSetupDTO);


                    model.setModelName(modelName);
                    model.setIoTPlatformAEPath(dataResourceSetupDTO.getIoTPlatformAEPath());
                    model.setIotPlatformstateSubList(dataResourceSetupDTO.getIotPlatformstateSubList());
                    model.setIoTPlatformtargetSubList(dataResourceSetupDTO.getIoTPlatformtargetSubList());
                    model.setIoTPlatformtrainDataContainerList(dataResourceSetupDTO.getIoTPlatformtrainDataContainerList());
                    model.setIoTPlatformtestDataContainerList(dataResourceSetupDTO.getIoTPlatformtestDataContainerList());
                    model.setCreateBy(ServiceCommonCode.MANAGER.getCode());
                    model.setSensorType(serviceRequestDTO.getSensorType());
                    model.setSensingStopCondition(serviceRequestDTO.getSensingStopCondition());
                    model.setSensorTargets(serviceRequestDTO.getSensorTargets());
                    model.setSensorList(dataResourceSetupDTO.getSensorList());


                    model.setOptimizationParamRange(serviceRequestDTO.getOptimizationParam());
                    model.setMlParamRange(serviceRequestDTO.getHyperParam());
                    model.setFilterParamRange(serviceRequestDTO.getFilterParam());
                    model.setSensorParamRange(serviceRequestDTO.getSensorParam());

                    model.setOptIter(0);

                    PropagationSetupRequestDTO propagationSetupRequestDTO = new PropagationSetupRequestDTO(serviceRequestDTO);

                    boolean setupBrokingResult = managementBrokerService.serviceSetup(serviceRequestDTO.getServiceId(), serviceRequestDTO);
                    log.info("setupBrokingResult is {}", setupBrokingResult);


                    try {
                        return Mono.just(modelDAO.insertModel(model))
                                .flatMap(savedModel -> {
                                    log.info("model Id is {}, optIter is {}", savedModel.getModelId(), savedModel.getOptIter());
                                    return Mono.just(savedModel.getModelId());
                                })
                                .flatMap(modelId -> {
                                    propagationSetupRequestDTO.setModelId(modelId);
                                    return Mono.just(propagationSetupRequestDTO);
                                })
                                .flatMapMany(result -> {
                                    try {
                                        Flux<String> mergedFlux = Flux.merge(
                                                provisioningService.setupModel(ServiceCommonCode.OPTIMIZATIONMANAGER.getCode(), result),
                                                provisioningService.setupModel(ServiceCommonCode.MLMANAGER.getCode(), result),
                                                provisioningService.setupModel(ServiceCommonCode.FILTERMANAGER.getCode(), result)
                                        );
                                        return mergedFlux.collectList().then(Mono.empty());
                                    } catch (JsonProcessingException | MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .then(engineService.setupEngine(propagationSetupRequestDTO.getServiceId(), ServiceZoneCode.ENGINE_IMAGE_NAME.getCode(),
                                        sensorStoragePath, ServiceManagerCode.ServiceZoneCode.SENSOR_CONTROLLER.getCode(),
                                        propagationSetupRequestDTO.getSensorType()))
                                .then(Mono.empty()); // Mono<Void> 반환
                    } catch (MalformedURLException | JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    public boolean runService(String modelName, String engineType) throws Exception {

        Model model = modelDAO.findModelByModelName(modelName);


        if (model == null) {
            log.error("serviceId is invalid");
            throw new Exception("Resource Not Exist");
        }

        Integer optIter = model.getOptIter();

        boolean parameterBrokingResult;

        try {
            if (engineType.equals(ServiceManagerCode.ServiceZoneCode.OPTIMIZATION_ENGINE.getCode())) {

                OptimizationEngineRunContentDTO engineRunContentDTO = new OptimizationEngineRunContentDTO();
                engineRunContentDTO.setControlMessage(ServiceManagerCode.ParameterBrokerCode.RUN_MESSAGE.getCode());

                parameterBrokingResult = managementBrokerService.engineRun(modelName, optIter, engineType, engineRunContentDTO);
            }
            else {


                Optimization optimization = optimizationDAO.findLastOptimizationByServiceId(modelName);
                EngineRunContentDTO engineRunContentDTO = new EngineRunContentDTO();

                engineRunContentDTO.setSensorParam(optimization.getSensorParam());
                engineRunContentDTO.setFilterParam(optimization.getFilterParam());
                engineRunContentDTO.setHyperParam(optimization.getHyperParam());
                engineRunContentDTO.setDataResourceInformation(optimization.getDataResourceInformation());
                engineRunContentDTO.setSensorType(model.getSensorType());
                engineRunContentDTO.setSensingStopCondition(model.getSensingStopCondition());



                parameterBrokingResult = managementBrokerService.engineRun(modelName, optIter, engineType, engineRunContentDTO);
            }
        } catch (Exception e) {
            return false;
        }
        return parameterBrokingResult;
    }

    public void updateOptimizationParameters(OptimizationParameters optimizationParameters) throws Exception {

        String serviceId = optimizationParameters.getServiceId();
        Model model = modelDAO.findModelByModelName(serviceId);


        if (model == null) {
            log.error("serviceId is invalid");
            throw new Exception("Resource Not Exist");
        }
        model.setOptIter(model.getOptIter()+1);
        Model savedModel = modelDAO.insertModel(model);
        log.info("re model Id is {}, optIter is {}", savedModel.getModelId(), savedModel.getOptIter());




        DataResourceInformation dataResourceInformation = new DataResourceInformation();

        DataResourceInformation.IoTPlatformInformation ioTPlatformInformation =
                new DataResourceInformation.IoTPlatformInformation();

        DataResourceInformation.DataHubInformation dataHubInformation =
                new DataResourceInformation.DataHubInformation();

        log.info("sensorList is {}", model.getSensorList());

        HashMap<String, List<String>> servicingDataResource =
                dataResourceService.servicingDataResource(
                serviceId,
                model.getSensorType(),
                model.getIoTPlatformtrainDataContainerList(),
                model.getIoTPlatformtestDataContainerList(),
                model.getSensorList(),
                optimizationParameters.getSensorParam());

        log.info("{}",servicingDataResource);

        ioTPlatformInformation.setTrainDataPath(servicingDataResource.get("trainDataPath"));
        ioTPlatformInformation.setTestDataPath(servicingDataResource.get("testDataPath"));
        ioTPlatformInformation.setTargetPath(model.getIoTPlatformtargetSubList());
        ioTPlatformInformation.setStatePath(model.getIotPlatformstateSubList());
        dataHubInformation.setRawTrainDataPath(servicingDataResource.get("rawTrainDataPath"));
        dataHubInformation.setRawTestDataPath(servicingDataResource.get("rawTestDataPath"));
        dataHubInformation.setFilterTrainDataPath(servicingDataResource.get("filterTrainDataPath"));
        dataHubInformation.setFilterTestDataPath(servicingDataResource.get("filterTestDataPath"));

        dataResourceInformation.setIoTPlatformInformation(ioTPlatformInformation);
        dataResourceInformation.setDataHubInformation(dataHubInformation);


        Optimization optimization = new Optimization();

        optimization.setServiceId(optimizationParameters.getServiceId());
        optimization.setSensorParam(optimizationParameters.getSensorParam());
        optimization.setFilterParam(optimizationParameters.getFilterParam());
        optimization.setHyperParam(optimizationParameters.getHyperParam());
        optimization.setDataResourceInformation(dataResourceInformation);
        optimization.setOptIter(model.getOptIter());

        optimizationDAO.insertOptimization(optimization);
    }





    public void deleteService(Long serviceId) {
        return;
    }


}