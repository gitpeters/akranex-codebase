package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IdentityPassService {
    ResponseEntity<CustomResponse> validateNG_Bvn(IdentityPassRequestPayload  request);
    ResponseEntity<CustomResponse> validateNG_Nin(IdentityPassRequestPayload  request);
    ResponseEntity<CustomResponse> validateNG_VotersCard(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateKE_NationalId(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateKE_Passport(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateKE_DriversLicense(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateNG_IntPassport(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateNG_DriverLicense(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateRequest(String countryCode, Map<String, Object> request);
    public IdentityPassRequestPayload mapToIdentityPassRequestPayload(LinkedHashMap<String, Object> map);
}
