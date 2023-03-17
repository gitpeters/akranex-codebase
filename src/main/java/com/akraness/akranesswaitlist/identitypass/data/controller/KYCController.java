package com.akraness.akranesswaitlist.identitypass.data.controller;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestNumber;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.data.service.IdentityPassService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/identitypass")
@RequiredArgsConstructor
public class KYCController {
    private final IdentityPassService identityPassService;

    @PostMapping("/kyc_validate")
    public ResponseEntity<?> validateRequest(@RequestBody Map<String, Object> request) throws JsonProcessingException {
        String countryCode = (String) request.get("countryCode");
        String dataType = (String) request.get("dataType");
        Object data = request.get("data");

        IdentityPassRequestPayload payload = identityPassService.mapToIdentityPassRequestPayload((LinkedHashMap<String, Object>) data);

        // Checking for the country code
        if ("NG".equals(countryCode)) {
            // Checking for the data type
            if ("bvn".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateBvn(payload);
                return ResponseEntity.ok().body(response.getBody());
            } else if ("nin".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateNin(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else if ("drivers_license".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateDriverLicense(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else if ("national_passport".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateIntPassport(payload);
                return ResponseEntity.ok().body(response.getBody());
            }else if ("voters_card".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateVotersCard(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else {
                // Invalid dataType for Nigeria
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("valid", false);
                countryValidation.put("message", "Invalid dataType for Nigeria");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation for Nigeria failed");
                response.put("countryValidation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }
        } else if("GH".equals(countryCode)){
            //TODO: create logic for other countries
            return null;
        }
        else {
            // Invalid countryCode
            Map<String, Object> countryValidation = new HashMap<>();
            countryValidation.put("valid", false);
            countryValidation.put("message", "Invalid countryCode");
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Validation failed");
            response.put("countryValidation", countryValidation);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
