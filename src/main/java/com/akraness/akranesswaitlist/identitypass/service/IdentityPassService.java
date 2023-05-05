package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IdentityPassService {
    ResponseEntity<CustomResponse> validateAndProccessVerification(Map<String, Object> request) throws JsonProcessingException, ExecutionException, InterruptedException, FirebaseMessagingException;

    void createPayload(Map<String, Object> payload) throws JsonProcessingException;

    Map<String, Object> getCountryDataPayload(String countryCode) throws JsonProcessingException;
}
