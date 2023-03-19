package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IdentityPassService {
    ResponseEntity<CustomResponse> validateNG_Bvn(IdentityPassRequestPayload  request);
    ResponseEntity<CustomResponse> validateNG_Nin(IdentityPassRequestPayload  request);
    ResponseEntity<CustomResponse> validateNG_VotersCard(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateNG_IntPassport(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateNG_DriverLicense(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateKE_NationalId(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateKE_Passport(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateKE_DriversLicense(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateGH_IntPassport(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateGH_VotersCard(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateGH_DriverLicense(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateUG_company(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateDocument(IdentityPassDocumentRequestPayload request);

    ResponseEntity<CustomResponse> validateZA_nationalId(IdentityPassRequestPayload request);
    ResponseEntity<CustomResponse> validateRequest(String countryCode, Map<String, Object> request);
    IdentityPassRequestPayload mapToIdentityPassRequestPayload(LinkedHashMap<String, Object> map);

    IdentityPassDocumentRequestPayload mapToIdentityPassDocumentRequestPayload(LinkedHashMap<String, Object> map);
}
