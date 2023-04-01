package com.akraness.akranesswaitlist.chimoney.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ChimoneyUtility {
    @Value("${chimoney.api-key}")
    private String apiKey;
    @Value("${chimoney.base-url}")
    private String baseUrl;

    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        return headers;
    }

    public String getBaseUrl(){ return this.baseUrl; }
}
