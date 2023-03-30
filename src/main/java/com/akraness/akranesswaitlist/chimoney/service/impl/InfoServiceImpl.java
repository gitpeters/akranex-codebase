package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.service.InfoService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final RestTemplateService restTemplateService;
    @Value("${chimoney.base-url}")
    private String baseUrl;

    @Value("${chimoney.api-key}")
    private String apiKey;
    @Override
    public ResponseEntity<CustomResponse> getCountryBanks(String countryCode) {
        String url = baseUrl + "info/country-banks?countryCode="+countryCode;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
