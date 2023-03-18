package com.akraness.akranesswaitlist.identitypass.data.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestNumber;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public ResponseEntity<CustomResponse> validateBvn(IdentityPassRequestPayload  request) {
        String url = dataBaseUrl + "bvn";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNin(IdentityPassRequestPayload  request) {
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

    @Override
    public ResponseEntity<CustomResponse> validateRequest(String countryCode, Map<String, Object> request) {
        String dataType = (String) request.get("dataType");
        Object data = request.get("data");
        String url = getUrl(countryCode, dataType);
        // checking if the url is null
        if (url == null) {
            return ResponseEntity.badRequest().body(new CustomResponse("false", "Invalid country code or data type"));
        }

        ResponseEntity<CustomResponse> response = restTemplateService.post(url, data, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    // Rerouting the api url based on the country code and data type
    private String getUrl(String countryCode, String dataType) {
        if ("NG".equals(countryCode)) {
            if ("bvn".equals(dataType)) {
                return dataBaseUrl + "bvn";
            } else if ("nin".equals(dataType)) {
                return dataBaseUrl + "nin";
            }else if("national_passport".equals(dataType)){
                return dataBaseUrl + "national_passport";
            }else if("drivers_license".equals(dataType)){
                return dataBaseUrl + "drivers_license/basic";
            }else if("voters_card".equals(dataType)){
                return baseUrl + "v1/biometrics/merchant/data/verification/voters_card";
            }
        } else if ("GH".equals(countryCode)) {
            if ("ssn".equals(dataType)) {
                return dataBaseUrl + "ssn";
            } else if ("tin".equals(dataType)) {
                return dataBaseUrl + "tin";
            }
        }
        return null;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.set("APP-ID", appId);

        return headers;
    }

    public IdentityPassRequestPayload mapToIdentityPassRequestPayload(LinkedHashMap<String, Object> map) {
        IdentityPassRequestPayload payload = new IdentityPassRequestPayload();
        payload.setNumber((String) map.get("number"));
        payload.setFirst_name((String) map.get("first_name"));
        payload.setLast_name((String) map.get("last_name"));
        payload.setState((String) map.get("state"));
        payload.setDob((String) map.get("dob"));
        payload.setNumber_nin((String) map.get("number_nin"));
        return payload;
    }
}
