package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IdentityPassService {


    ResponseEntity<CustomResponse> validateDocument(Map<String, Object> request);
    ResponseEntity<CustomResponse> processDataVerification(Map<String, Object> request);
    ResponseEntity<CustomResponse> validateRequest(Map<String, Object> request);

}
