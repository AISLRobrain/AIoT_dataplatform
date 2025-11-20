package aisl.ksensor.servicemanager.common.transfer.http.service;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface HttpRequest {

    public Mono<String> httpPostRequest(String uri, String body, Map<String, String> headers);

    public Mono<String> httpGetRequest(String uri, Map<String, String> headers);
}
