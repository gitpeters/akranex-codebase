package com.akraness.akranesswaitlist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateService {
    private RestTemplate restTemplate;

    @Autowired
    public RestTemplateService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    public <T> ResponseEntity<CustomResponse> post(String url, T req, HttpHeaders headers) {

        HttpEntity entity = headers == null ? new HttpEntity(req) : new HttpEntity(req, headers);
        ResponseEntity<CustomResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CustomResponse.class);
        return response;
    }

    public ResponseEntity<CustomResponse> get(String url, HttpHeaders headers) {
        HttpEntity entity = headers == null ? null : new HttpEntity(headers);
        ResponseEntity<CustomResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, CustomResponse.class);

        return response;
    }

    public ResponseEntity<CustomResponse> delete(String url, HttpHeaders headers) {
        HttpEntity entity = headers == null ? null : new HttpEntity(headers);
        ResponseEntity<CustomResponse> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, CustomResponse.class);

        return response;
    }
}
