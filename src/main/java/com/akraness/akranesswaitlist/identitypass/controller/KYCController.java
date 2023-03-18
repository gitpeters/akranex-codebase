package com.akraness.akranesswaitlist.identitypass.controller;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.service.IdentityPassService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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

        // Check if data is null
        if (data == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Validation failed");
            response.put("error", "Request data is null");
            return ResponseEntity.badRequest().body(response);
        }
        IdentityPassRequestPayload payload = identityPassService.mapToIdentityPassRequestPayload((LinkedHashMap<String, Object>) data);

        // Checking for the country code
        if ("NG".equals(countryCode)) {
            // Checking for the data type
            if ("bvn".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateNG_Bvn(payload);
                return ResponseEntity.ok().body(response.getBody());
            } else if ("nin".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateNG_Nin(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else if ("drivers_license".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateNG_DriverLicense(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else if ("national_passport".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateNG_IntPassport(payload);
                return ResponseEntity.ok().body(response.getBody());
            }else if ("voters_card".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateNG_VotersCard(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else {
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("valid", false);
                countryValidation.put("message", "Invalid data type:"+dataType+" for Nigeria");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation for Nigeria failed");
                response.put("validation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }//end of Nigeria verification

        }
        else if("KE".equals(countryCode)){
            if ("national_identity".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateKE_NationalId(payload);
                return ResponseEntity.ok().body(response.getBody());
            } else if ("passport".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateKE_Passport(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else if ("drivers_license".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateKE_DriversLicense(payload);
                return ResponseEntity.ok().body(response.getBody());
            }else{
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("valid", false);
                countryValidation.put("message", "Invalid data type: "+dataType+" for Kenya");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation for Kenya failed");
                response.put("validation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }
        }// end of Kenya verification
        else if("GH".equals(countryCode)){
            if ("national_passport".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateGH_IntPassport(payload);
                return ResponseEntity.ok().body(response.getBody());
            } else if ("voters_card".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateGH_VotersCard(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else if ("drivers_license".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateGH_DriverLicense(payload);
                return ResponseEntity.ok().body(response.getBody());
            }else{
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("valid", false);
                countryValidation.put("message", "Invalid data type: "+dataType+" for Ghana");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation for Ghana failed");
                response.put("validation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }
        }// end of Ghana verification
        else if("UG".equals(countryCode)){
            if ("company".equals(dataType)) {
                ResponseEntity<CustomResponse> response = identityPassService.validateUG_company(payload);
                return ResponseEntity.ok().body(response.getBody());
            }else{
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("valid", false);
                countryValidation.put("message", "Invalid data type: "+dataType+" for Uganda");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation for Uganda failed");
                response.put("validation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }
        }// end of Uganda verification
        else if("ZA".equals(countryCode)){
            if ("national_id".equals(dataType)){
                ResponseEntity<CustomResponse> response = identityPassService.validateZA_nationalId(payload);
                return ResponseEntity.ok().body(response.getBody());
            }
            else{
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("valid", false);
                countryValidation.put("message", "Invalid data type: "+dataType+" for South Africa");
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation for South Africa");
                response.put("validation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }
        }// end of South Africa verification
        else {
            // Invalid countryCode
            Map<String, Object> countryValidation = new HashMap<>();
            countryValidation.put("valid", false);
            countryValidation.put("message", "Invalid country code: "+countryCode);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Validation failed");
            response.put("validation", countryValidation);
            return ResponseEntity.badRequest().body(response);
        }

        // Document Verficiation
    }
}
