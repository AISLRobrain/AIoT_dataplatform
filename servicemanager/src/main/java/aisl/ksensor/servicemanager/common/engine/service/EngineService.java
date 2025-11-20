package aisl.ksensor.servicemanager.common.engine.service;

import aisl.ksensor.servicemanager.common.engine.data.dto.EngineDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.util.List;

public interface EngineService {

    public Mono<EngineDTO> setupEngine(String modelName, String resource, List<String> volumePath, String engineType, String sensorType) throws MalformedURLException, JsonProcessingException;
    public Mono<String> createEngine(String jwt, EngineDTO engineDTO, List<String> volumePath, String engineType) throws JsonProcessingException, MalformedURLException;

    public Mono<String> authEngine(String username, String password) throws JsonProcessingException, MalformedURLException;

    public Mono<String> confirmEngine(String jwt, String engineId) throws JsonProcessingException, MalformedURLException;

    public Mono<String> confirmImage(String jwt, String ImageName) throws JsonProcessingException, MalformedURLException;

    public Mono<String> startEngine(String jwt, String engineId);

    public void stopEngine(Long engineId);

    public void deleteEngine(Long engineId);
}
