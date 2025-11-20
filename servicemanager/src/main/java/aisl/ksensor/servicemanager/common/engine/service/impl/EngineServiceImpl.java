package aisl.ksensor.servicemanager.common.engine.service.impl;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ErrorCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ServiceCommonCode;
import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ServiceZoneCode;
import aisl.ksensor.servicemanager.common.engine.data.dto.ApiAuthDTO;
import aisl.ksensor.servicemanager.common.engine.data.dto.EngineConfigDTO;
import aisl.ksensor.servicemanager.common.engine.data.dto.EngineConfigDTO.*;
import aisl.ksensor.servicemanager.common.engine.data.dto.EngineDTO;
import aisl.ksensor.servicemanager.common.engine.service.EngineService;
import aisl.ksensor.servicemanager.common.exception.EngineException;
import aisl.ksensor.servicemanager.common.handler.EngineExceptionHandler;
import aisl.ksensor.servicemanager.common.transfer.http.service.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
public class EngineServiceImpl implements EngineService {

    @Value("${url.servicezone.http}")
    private String SERVICEZONEURL;

    @Value("${servicezone.api.auth.username}")
    private String SERVICEZONE_API_AUTH_USERNAME;

    @Value("${servicezone.api.auth.password}")
    private String SERVICEZONE_API_AUTH_PASSWORD;

    @Autowired
    private HttpRequest httpRequest;

    private final EngineExceptionHandler engineExceptionHandler = new EngineExceptionHandler();


    public Mono<EngineDTO> setupEngine(String modelName, String imageName,
                                       List<String> volumePath, String engineType, String sensorType) throws MalformedURLException, JsonProcessingException {
        log.info("setupEngine");


        EngineDTO engineDTO = new EngineDTO();

//        TODO get adminName and passWord by own authentication
//        System.out.println(modelName);
        engineDTO.setEngineContainerName(modelName);
        engineDTO.setEngineType(engineType);
        engineDTO.setEngineNetworkName("engine_network");
        engineDTO.setOpenPort(ServiceZoneCode.SERVICE_ZONE_OPTIMIZATIONENGINE_PORT.getCode());
        engineDTO.setAllocatedCpuCores(ServiceZoneCode.SERVICE_ZONE_ENGINE_CPU_CORE.getCode());
        engineDTO.setAllocatedMemory(ServiceZoneCode.SERVICE_ZONE_ENGINE_RAM_MEMORY.getLongValue());
        engineDTO.setSensorType(sensorType);

        System.out.println("engineType: " + engineType);

        if (engineType.equals(ServiceZoneCode.ML_ENGINE.getCode())) {
//            Todo.. set the ml engine gpu dynamically
            engineDTO.setAllocatedGpuCounts(ServiceZoneCode.SERVICE_ZONE_MLENGINE_GPU_COUNT.getIntValue());
            engineDTO.setAllocatedGpuMemory(ServiceZoneCode.SERVICE_ZONE_MLENGINE_GPU_MEMORY.getCode());
        }
        else {
            engineDTO.setAllocatedGpuCounts(ServiceZoneCode.SERVICE_ZONE_ENGINE_GPU_COUNT.getIntValue());
            engineDTO.setAllocatedGpuMemory(ServiceZoneCode.SERVICE_ZONE_ENGINE_GPU_MEMORY.getCode());
        }
        engineDTO.setAllocatedGpuCounts(ServiceZoneCode.SERVICE_ZONE_ENGINE_GPU_COUNT.getIntValue());
        engineDTO.setAllocatedGpuMemory(ServiceZoneCode.SERVICE_ZONE_ENGINE_GPU_MEMORY.getCode());
        engineDTO.setAdminName(SERVICEZONE_API_AUTH_USERNAME);
        engineDTO.setAdminPassword(SERVICEZONE_API_AUTH_PASSWORD);
        engineDTO.setEnvironmentID(ServiceZoneCode.SERVICE_ZONE_ENGINE_ENVIRONMENT_ID.getIntValue());
        engineDTO.setCreateBy(ServiceCommonCode.MANAGER.getCode());

        log.info("engineDTO: " + engineDTO);

        return authEngine(engineDTO.getAdminName(), engineDTO.getAdminPassword())
                .flatMap(jwt -> engineExceptionHandler.handleAuthEngineError(jwt)
                        .flatMap(authJwt -> {
                            engineDTO.setAccessToken(authJwt);
                            return confirmImage(jwt, imageName)
                                    .flatMap(engine -> engineExceptionHandler.handleConfirmImageError(engine)
                                            .flatMap(confirmEngine -> {
                                                    engineDTO.setImageId(imageName);
                                                try {
                                                    return createEngine(jwt, engineDTO, volumePath, engineType)
                                                            .flatMap(createdEngine -> engineExceptionHandler.handleCreateEngineError(createdEngine)
                                                                    .flatMap(created -> {
                                                                        engineDTO.setEngineId(created);
                                                                        return startEngine(jwt, created)
                                                                                .map(result -> engineDTO);

                                                                    }));
                                                } catch (JsonProcessingException | MalformedURLException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }));
                        }));
    }


    public Mono<String> authEngine(String username, String password) throws JsonProcessingException, MalformedURLException {
        log.info("authEngine");

        ApiAuthDTO apiAuthDTO = new ApiAuthDTO();
        apiAuthDTO.setUsername(username);
        apiAuthDTO.setPassword(password);

        ObjectMapper authEngineObjectMapper = new ObjectMapper();
        ObjectMapper responseEngineObjectMapper = new ObjectMapper();

        String authEngineBody = authEngineObjectMapper.writeValueAsString(apiAuthDTO);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try {
            URL serviceZoneURL = new URL(SERVICEZONEURL + ServiceZoneCode.SERVICE_ZONE_AUTH_ENDPOINT.getCode());
            return httpRequest.httpPostRequest(serviceZoneURL.toString(), authEngineBody, headers)
                    .onErrorResume(e -> {
                        throw new EngineException(ErrorCode.INVALID_AUTHORIZATION, "Error authorizing user.", e);
                    })
                    .map(responseBody -> {
                        // JSON 파싱하여 "jwt" 값 추출
                        try {
                            JsonNode responseJson = responseEngineObjectMapper.readTree(responseBody);
                            String jwt = responseJson.get("jwt").asText();
                            return jwt;
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to parse JSON response doing extract jwt", e);
                        }
                    });
        } catch (MalformedURLException e) {
            throw new EngineException(ErrorCode.MALFORMED_URL, "Malformed URL.", e);
        }
    }

    public Mono<String> confirmImage(String jwt, String imageName) {
        log.info("findImage");

        ObjectMapper responseEngineObjectMapper = new ObjectMapper();

        String endpoint = SERVICEZONEURL + ServiceZoneCode.SERVICE_ZONE_CONFIRM_IMAGE_ENDPOINT.getCode();
        endpoint = String.format(endpoint, imageName);
//        System.out.println("endpoint: " + endpoint);
        try {
            URL serviceZoneURL = new URL(endpoint);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + jwt);
//            System.out.println(headers);

            return httpRequest.httpGetRequest(serviceZoneURL.toString(), headers)
                    .onErrorResume(e -> {
                        throw new EngineException(ErrorCode.NOT_EXISTS_IMAGE, "Error confirming the image.", e);
                    })
                    .map(responseBody -> {
                        // JSON 파싱하여 "jwt" 값 추출
                        try {
                            JsonNode responseJson = responseEngineObjectMapper.readTree(responseBody);
                            String imageId = responseJson.get("Id").asText();
                            return imageId;
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to parse JSON response doing extract jwt", e);
                        }
                    });
        } catch (MalformedURLException e) {
            throw new EngineException(ErrorCode.MALFORMED_URL, "Malformed URL.", e);
        }
    }


    public Mono<String> confirmEngine(String jwt, String engineName) {
        log.info("findEngine");

        String endpoint = SERVICEZONEURL + ServiceZoneCode.SERVICE_ZONE_CONFIRM_CONTAINER_ENDPOINT.getCode();
        endpoint = String.format(endpoint, engineName);
        try {
            URL serviceZoneURL = new URL(endpoint);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + jwt);

            return httpRequest.httpGetRequest(serviceZoneURL.toString(), headers)
                    .onErrorResume(e -> {
                        throw new EngineException(ErrorCode.ENGINE_SETUP_ERROR, "Error confirming the engine.", e);
                    });
        } catch (MalformedURLException e) {
            throw new EngineException(ErrorCode.MALFORMED_URL, "Malformed URL.", e);
        }
    }



    public Mono<String> createEngine(String jwt, EngineDTO engineDTO,
                                     List<String> volumePath, String engineType) throws JsonProcessingException, MalformedURLException {
        log.info("createEngine");

        EngineConfigDTO engineConfigDTO = new EngineConfigDTO();
        engineConfigDTO.setName(engineDTO.getEngineContainerName());

        System.out.println("engineType: " + engineType);

        engineConfigDTO.setImage(engineDTO.getImageId());

        List<String> envList = new ArrayList<>();
        envList.add("SERVICE_ID=" + engineDTO.getEngineContainerName());
        envList.add("SENSOR_TYPE=" + engineDTO.getSensorType());
        engineConfigDTO.setEnv(envList);


        NetworkingConfig networkingConfig = new NetworkingConfig();
        HashMap<String, Object> endpointsConfigMap = new HashMap<>();
        endpointsConfigMap.put(engineDTO.getEngineNetworkName(), new HashMap<>());
        networkingConfig.setEndpointsConfig(endpointsConfigMap);
        engineConfigDTO.setNetworkingConfig(networkingConfig);

        HashMap<String, Object> exposedPortsMap = new HashMap<>();
        exposedPortsMap.put(engineDTO.getOpenPort(), new HashMap<>());
        engineConfigDTO.setExposedPorts(exposedPortsMap);

        HostConfig hostConfig = new HostConfig();

//        List<String> updatedVolumePath = volumePath.stream()
//                .map(str -> str + ServiceCommonCode.ENGINE_MOUNT_DIRECTORY.getCode()).toList();
//
//        System.out.println("volumeMounted path is " + updatedVolumePath);
//        hostConfig.setBinds(updatedVolumePath);

        HashMap<String, List<Object>> portBindingsMap = new HashMap<>();
        portBindingsMap.put(ServiceZoneCode.SERVICE_ZONE_OPTIMIZATIONENGINE_PORT.getCode(), Collections.emptyList());
        hostConfig.setPortBindings(portBindingsMap);

        Resources resources = new Resources();
        resources.setCpusetCpus(engineDTO.getAllocatedCpuCores());
        resources.setMemory(engineDTO.getAllocatedMemory());

        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setDriver("");
        deviceRequest.setCount(engineDTO.getAllocatedGpuCounts());
        deviceRequest.setDeviceIDs(Collections.emptyList());
        deviceRequest.setCapabilities(Collections.singletonList(Arrays.asList("gpu", "nvidia", "compute")));

        Options options = new Options();
        options.setComNvidiaLimit("memory="+engineDTO.getAllocatedGpuMemory());
        deviceRequest.setOptions(options);

        resources.setDeviceRequests(Collections.singletonList(deviceRequest));
        hostConfig.setResources(resources);

        engineConfigDTO.setHostConfig(hostConfig);


        // ObjectMapper를 사용하여 DTO를 JSON 문자열로 변환
        ObjectMapper createEngineObjectMapper = new ObjectMapper();

        String createEngineBody = createEngineObjectMapper.writeValueAsString(engineConfigDTO);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + jwt);

        try {
            URL serviceZoneURL = new URL(SERVICEZONEURL + ServiceZoneCode.SERVICE_ZONE_CREATE_ENGINE_ENDPOINT.getCode());
            return httpRequest.httpPostRequest(serviceZoneURL.toString(), createEngineBody, headers)
                    .map(responseBody -> {
                        try {
                            JsonNode responseJson = createEngineObjectMapper.readTree(responseBody);
                            String containerId = responseJson.get("Id").asText();
                            return containerId;
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to parse JSON response doing extract containerId", e);
                        }
                    });
        } catch (MalformedURLException e) {
            throw new EngineException(ErrorCode.MALFORMED_URL, "Malformed URL.", e);
        }
    }

    public Mono<String> startEngine(String jwt, String engineId) {
        log.info("startEngine");


        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwt);

        String endpoint = SERVICEZONEURL + ServiceZoneCode.SERVICE_ZONE_START_ENGINE_ENDPOINT.getCode();
        endpoint = String.format(endpoint, engineId);

        try {
            URL serviceZoneURL = new URL(endpoint);
            System.out.println(serviceZoneURL);
            System.out.println(headers);
            return httpRequest.httpPostRequest(serviceZoneURL.toString(), null, headers)
                    .onErrorResume(e -> {
                        throw new EngineException(ErrorCode.ENGINE_START_ERROR, "Error start error.", e);
                    });
        } catch (MalformedURLException e) {
            throw new EngineException(ErrorCode.MALFORMED_URL, "Malformed URL.", e);
        }
    }

    public void stopEngine(Long EngineId) {
        log.info("stopEngine");
    }

    public void deleteEngine(Long EngineId) {
        log.info("deleteEngine");
    }

}