package com.henrique.nookio_api.shared.external_communication;

import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class ApiClient {

    private final RestClient client;

    private ApiClient(String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());

        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public static ApiClient of(String baseUrl) {
        return new ApiClient(baseUrl);
    }

    public Map<HttpStatusCode, Object> request(HttpMethod method, String uri, Object body) {
        String debugId = LogContext.getDebugId();
        log.info("[EXTERNAL_HTTP_REQUEST] debugId={} method={} uri={}", debugId, method, uri);

        try {
            RestClient.RequestBodySpec requestSpec = client.method(method).uri(uri);

            if (body != null) {
                requestSpec.body(body);
            }

            ResponseEntity<Object> response = requestSpec.retrieve().toEntity(Object.class);
            Object responseBody = response.getBody() != null ? response.getBody() : Collections.emptyMap();
            log.info("[EXTERNAL_HTTP_RESPONSE] debugId={} status={}", debugId, response.getStatusCode());

            return Map.of(response.getStatusCode(), responseBody);
        } catch (Exception e) {
            log.error("[EXTERNAL_HTTP_ERROR] debugId={} method={} uri={} error={}", debugId, method, uri, e.getMessage());
            throw e;
        }
    }
}
