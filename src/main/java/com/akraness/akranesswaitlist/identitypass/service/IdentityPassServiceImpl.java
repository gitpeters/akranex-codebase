package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequest;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.repository.IdentityPassRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityPassServiceImpl implements IdentityPassService {
    private final RestTemplateService restTemplateService;

    private final IdentityPassRepo identityPassRepo;

    @Value("${myidentitypass.api-key}")
    private String apiKey;
    @Value("${myidentitypass.app-id}")
    private String appId;
    @Value("${myidentitypass.base-url}")
    private String baseUrl;

    @Value("${myidentitypass.data-base-bvn-url}")
    private String dataBaseUrl;

    // Validation for Nigeria
    @Override
    public ResponseEntity<CustomResponse> validateNG_Bvn(IdentityPassRequestPayload  request) {
        String url = dataBaseUrl + "bvn";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        if (request == null) {
            return ResponseEntity.badRequest().body(new CustomResponse("false", "Invalid country code or data type"));
        }
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_Nin(IdentityPassRequestPayload  request) {
        String url = dataBaseUrl + "nin_wo_face";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_VotersCard(IdentityPassRequestPayload request) {
        String url = baseUrl + "v1/biometrics/merchant/data/verification/voters_card";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    // Validation for Kenya
    @Override
    public ResponseEntity<CustomResponse> validateKE_NationalId(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ke/national_id";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateKE_Passport(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ke/passportK";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateKE_DriversLicense(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ke/drivers_licensek";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_IntPassport(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "national_passport";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_DriverLicense(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "drivers_license/basic";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateRequest(String countryCode, Map<String, Object> request) {
        String dataType = (String) request.get("dataType");
        Object data = request.get("data");
        String url = getUrl(countryCode, dataType);
        //Save payload and country code to db
        IdentityPassRequest identityPass = new IdentityPassRequest();
        identityPass.setCountryCode(countryCode);
        identityPass.setPayload((String) data);
        log.info("Saving {} and {} to database", countryCode, data);
        identityPassRepo.save(identityPass);
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
        payload.setFirstname((String) map.get("firstname"));
        payload.setLastname((String) map.get("lastname"));
        payload.setNationalid((String) map.get("nationalid"));
        return payload;
    }
}
