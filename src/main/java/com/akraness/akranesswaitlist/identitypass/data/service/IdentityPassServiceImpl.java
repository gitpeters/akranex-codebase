package com.akraness.akranesswaitlist.identitypass.data.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestNumber;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentityPassServiceImpl implements IdentityPassService {
    private final RestTemplateService restTemplateService;

    @Value("${myidentitypass.api-key}")
    private String apiKey;
    @Value("${myidentitypass.app-id}")
    private String appId;
    @Value("${myidentitypass.base-url}")
    private String baseUrl;

    @Value("${myidentitypass.data-base-bvn-url}")
    private String dataBaseUrl;

    @Override
    public ResponseEntity<CustomResponse> validateBvn(IdentityPassRequestNumber request) {
        String url = dataBaseUrl + "bvn";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNin(IdentityPassRequestNumber request) {
        String url = dataBaseUrl + "nin_wo_face";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateVotersCard(IdentityPassRequestPayload request) {
        String url = baseUrl + "v1/biometrics/merchant/data/verification/voters_card";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateIntPassport(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "national_passport";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateDriverLicense(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "drivers_license/basic";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.set("APP-ID", appId);

        return headers;
    }
}
