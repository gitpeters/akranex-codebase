package com.akraness.akranesswaitlist.identitypass.data.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestNumber;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IdentityPassService {
    ResponseEntity<CustomResponse> validateBvn(IdentityPassRequestPayload  request);

    ResponseEntity<CustomResponse> validateNin(IdentityPassRequestPayload  request);
    ResponseEntity<CustomResponse> validateVotersCard(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateIntPassport(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateDriverLicense(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateRequest(String countryCode, Map<String, Object> request);
    public IdentityPassRequestPayload mapToIdentityPassRequestPayload(LinkedHashMap<String, Object> map);
}
