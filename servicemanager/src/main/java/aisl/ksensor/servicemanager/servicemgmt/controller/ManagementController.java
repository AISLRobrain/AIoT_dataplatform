package aisl.ksensor.servicemanager.servicemgmt.controller;

import aisl.ksensor.servicemanager.dataresource.data.dto.DatasetDTO;
import aisl.ksensor.servicemanager.dataresource.service.DataResourceService;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.EAIEngineRunRequestDTO;
import aisl.ksensor.servicemanager.common.data.dto.OptimizationParameters;
import aisl.ksensor.servicemanager.servicemgmt.data.dto.ServiceSetupRequestDTO;
import aisl.ksensor.servicemanager.servicemgmt.service.ManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "최적화 서비스", description = "최적화 서비스 관련 API")
@RestController
@Slf4j
public class ManagementController {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ManagementService managementService;

    @Autowired
    DataResourceService dataResourceService;

    /**
     * create dataModel
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param serviceRequestDTO create dataModel data
     * @throws Exception create error
     */
    @Operation(summary = "서비스 등록 및 프로비저닝", description = "서비스 등록 및 프로비저닝 메서드")
    @PostMapping("/setup")
    public @ResponseBody void setupService(HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestBody ServiceSetupRequestDTO serviceRequestDTO) throws Exception {
        log.info("SetupOptimization Request msg: '{}'", serviceRequestDTO);

        // 1. Validation
        // TODO: Essential Value Check

        // 2. Duplicate Check
        String retrieveServiceId = serviceRequestDTO.getServiceId();
//        if (retrieveServiceId != null) {
//            throw new Exception("Duplicate Service ID");
//        }

        managementService.setupService(serviceRequestDTO).subscribe();
    }

    /**
     * provisioning
     * @param request
     * @param response
     * @param engineType
     * @param engineRunRequestDTO
     * @throws Exception
     */
    @Operation(summary = "엔진 run", description = "엔진 run 메서드")
    @PostMapping("/run/{serviceType}")
    public @ResponseBody void runOptimization(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @PathVariable("serviceType") String engineType,
                                              @RequestBody EAIEngineRunRequestDTO engineRunRequestDTO) throws Exception {

        log.info("Run Request Engine Type: {}", engineType);
        log.info("RunOptimization Request msg= '{}'", engineRunRequestDTO);

        boolean sendMessageResponse = managementService.runService(engineRunRequestDTO.getServiceId(), engineType);
    }

    @PostMapping("/update/parameters")
    public @ResponseBody void updateParameters(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @RequestBody OptimizationParameters optimizationDTO) throws Exception {

        log.info("Update Parameter Request {}", optimizationDTO);

        managementService.updateOptimizationParameters(optimizationDTO);
    }

    @PostMapping("/testapi")
    public @ResponseBody void testapi(HttpServletRequest request,
                                               HttpServletResponse response,
                                               @RequestBody DatasetDTO datasetDTO) throws Exception {

        log.info("{}", datasetDTO);

        dataResourceService.postDataset(datasetDTO.getId(), datasetDTO.getDataModelId()).subscribe();


    }

//    /**
//     * provisioning
//     * @param request
//     * @param response
//     * @param sensorControllerRunDTO
//     * @throws Exception
//     */
//    @Operation(summary = "센서 run", description = "센서 run 메서드")
//    @PostMapping("/run/sensor")
//    public @ResponseBody void runSensor(HttpServletRequest request,
//                                              HttpServletResponse response,
//                                              @RequestBody SensorControllerRunDTO sensorControllerRunDTO) throws Exception {
//
//        log.info("RunSensor Request msg= '{}'", sensorControllerRunDTO);
//    }
}
