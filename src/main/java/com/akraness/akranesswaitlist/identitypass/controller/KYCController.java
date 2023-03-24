package com.akraness.akranesswaitlist.identitypass.controller;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.service.IdentityPassService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/identitypass")
@RequiredArgsConstructor
public class KYCController {
    private final IdentityPassService identityPassService;

    @PostMapping("/kyc-validate")
    public ResponseEntity<?> validateRequest(@RequestBody Map<String, Object> request) throws JsonProcessingException {
        Object data = request.get("data");

        // Check if data is null
        if (data == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Validation failed");
            response.put("error", "Request data is null");
            return ResponseEntity.badRequest().body(response);
        }

        return identityPassService.validateAndProccessVerification(request);

    }

//    @PostMapping("/create-country-verification-payload")
//    public String createPayLoad(@RequestBody Map<String, Object> payload) throws JsonProcessingException {
//        identityPassService.createPayload(payload);
//
//        return "";
//    }

    @GetMapping("/get-country-data-payload")
    public Map<String, Object> getCountryDataPayload(@RequestParam("countryCode") String CountryCode) throws JsonProcessingException {
        return identityPassService.getCountryDataPayload(CountryCode);

    }
}
