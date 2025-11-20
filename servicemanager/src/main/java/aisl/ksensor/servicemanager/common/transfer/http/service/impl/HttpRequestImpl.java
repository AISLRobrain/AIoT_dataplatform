package aisl.ksensor.servicemanager.common.transfer.http.service.impl;

import aisl.ksensor.servicemanager.common.code.ServiceManagerCode.ErrorCode;
import aisl.ksensor.servicemanager.common.exception.HttpException;
import aisl.ksensor.servicemanager.common.transfer.http.service.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.Map;

@Service
@Slf4j
public class HttpRequestImpl implements HttpRequest {

    public Mono<String> httpPostRequest(String uri, String body, Map<String, String> headers) {
        WebClient webClient = WebClient.create();
        WebClient.RequestHeadersSpec<?> request;

        if (body != null) {
            request = webClient.post()
                    .uri(uri)
                    .bodyValue(body);
        } else {
            request = webClient.post()
                    .uri(uri)
                    .body(Mono.empty(), String.class);
        }

        headers.forEach(request::header);

        return request.retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Error occurred during the Post request: {}", e.getMessage());
                    return Mono.error(new HttpException(ErrorCode.HTTP_POST_CLIENT_ERROR, "Post error message"));
                });
    }

    public Mono<String> httpGetRequest(String uri, Map<String, String> headers) {
        WebClient webClient = WebClient.create();

        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri(uri);

        headers.forEach(request::header);

        return request.retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    log.error("Error occurred during the GET request: {}", e.getMessage());
                    return Mono.error(new HttpException(ErrorCode.HTTP_GET_CLIENT_ERROR, "GET error message"));
                });
    }

}

