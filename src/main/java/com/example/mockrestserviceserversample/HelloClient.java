package com.example.mockrestserviceserversample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HelloClient {

    private final RestTemplate restTemplate;

    public HelloClient(RestTemplateBuilder restTemplateBuilder, @Value("${hello-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.rootUri(baseUrl).build();
    }

    public HelloResponse getHello() {
        ResponseEntity<HelloResponse> responseEntity = restTemplate.getForEntity("/api/hello", HelloResponse.class);
        if (responseEntity.getStatusCode().isError()) {
            throw new RuntimeException("error");
        }
        return responseEntity.getBody();
    }

    public String postHello(HelloRequest request) {
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/api/hello", request, Void.class);
        if (responseEntity.getStatusCode().isError()) {
            throw new RuntimeException("error");
        }
        return "OK";
    }
}
