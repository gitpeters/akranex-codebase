package com.akraness.akranesswaitlist.identitypass.controller;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
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
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/identitypass")
@RequiredArgsConstructor
public class KYCController {
    private final IdentityPassService identityPassService;

    @PostMapping("/kyc_validate")
    public ResponseEntity<?> validateRequest(@RequestBody Map<String, Object> request) throws JsonProcessingException {
        String countryCode = (String) request.get("countryCode");
        String type = (String) request.get("type");
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
        IdentityPassDocumentRequestPayload documentPayload = identityPassService.mapToIdentityPassDocumentRequestPayload((LinkedHashMap<String, Object>) data);

        // Countries using the application that support document verification
        Map<String, Function<IdentityPassDocumentRequestPayload, ResponseEntity<CustomResponse>>> validationMethods = new HashMap<>();
        validationMethods.put("NG", identityPassService::validateDocument);//NIGERIA
        validationMethods.put("KE", identityPassService::validateDocument);//KENYA
        validationMethods.put("GH", identityPassService::validateDocument);//GHANA
        validationMethods.put("ZA", identityPassService::validateDocument);//SOUTH AFRICA
        validationMethods.put("TZ", identityPassService::validateDocument);//TANZANIA
        validationMethods.put("ET", identityPassService::validateDocument);//ETHIOPIA
        validationMethods.put("CD", identityPassService::validateDocument);//CONGO
        validationMethods.put("GA", identityPassService::validateDocument);//GABON
        validationMethods.put("BJ", identityPassService::validateDocument);//BENIN
        validationMethods.put("RW", identityPassService::validateDocument);//RWANDA
        validationMethods.put("SL", identityPassService::validateDocument);//SIERRA LEONE
        validationMethods.put("UG", identityPassService::validateDocument);//UGANDA
        validationMethods.put("ZM", identityPassService::validateDocument);//ZAMBIA
        validationMethods.put("US", identityPassService::validateDocument);//UNITED STATES OF AMERICA
        validationMethods.put("CI", identityPassService::validateDocument);//COTE D'IVOIRE
        validationMethods.put("CA", identityPassService::validateDocument);//CANADA
        validationMethods.put("IN", identityPassService::validateDocument);//INDIA
        validationMethods.put("GB", identityPassService::validateDocument);//UNITED KINGDOM
        validationMethods.put("DE", identityPassService::validateDocument);//GERMANY
        validationMethods.put("FR", identityPassService::validateDocument);//FRANCE
        validationMethods.put("NL", identityPassService::validateDocument);//NETHERLANDS
        validationMethods.put("IE", identityPassService::validateDocument);//IRELAND
        validationMethods.put("ES", identityPassService::validateDocument);//SPAIN
        validationMethods.put("SE", identityPassService::validateDocument);//SWEDEN
        validationMethods.put("DK", identityPassService::validateDocument);//DENMARK
        validationMethods.put("IT", identityPassService::validateDocument);//ITALY
        validationMethods.put("PT", identityPassService::validateDocument);//PORTUGAL
        validationMethods.put("PL", identityPassService::validateDocument);//POLAND
        validationMethods.put("LT", identityPassService::validateDocument);//LITHUANIA
        validationMethods.put("LV", identityPassService::validateDocument);//LATVIA
        validationMethods.put("EE", identityPassService::validateDocument);//ESTONIA
        validationMethods.put("NO", identityPassService::validateDocument);//NORWAY

        // Expected data type to be supply for document verficiation
        List<String> validDataTypes =
                Arrays.asList("bvn", "national_id",
                        "national_passport", "nin",
                        "drivers_license", "voters_card",
                        "national_identity");

        if("data".equals(type)){
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
                Map<String, Object> countryValidation = new HashMap<>();
                countryValidation.put("message", "Invalid country code: " + countryCode);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Validation failed");
                response.put("validation", countryValidation);
                return ResponseEntity.badRequest().body(response);
            }
        }else if("document".equals(type)){
            if (validationMethods.containsKey(countryCode)) {
                if (validDataTypes.contains(dataType)) {
                    ResponseEntity<CustomResponse> response = validationMethods.get(countryCode).apply(documentPayload);
                    return ResponseEntity.ok().body(response.getBody());
                } else {
                    Map<String, Object> countryValidation = new HashMap<>();
                    countryValidation.put("valid", false);
                    countryValidation.put("message", "Invalid data type: " + dataType);
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Validation for " + countryCode);
                    response.put("validation", countryValidation);
                    return ResponseEntity.badRequest().body(response);
                }
            }else{
                Map<String, Object> dataSupportCountry = new HashMap<>();
                dataSupportCountry.put("valid", false);
                dataSupportCountry.put("message", type+" not supported for this country");
                Map<String, Object> response = new HashMap<>();
                response.put("Validation", dataSupportCountry);
                return ResponseEntity.badRequest().body(response);
            }
        }
        else{
            Map<String, Object> countryValidation = new HashMap<>();
            countryValidation.put("message", "Invalid type: " + type);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Validation failed");
            response.put("validation", countryValidation);
            return ResponseEntity.badRequest().body(response);
        }

    }
}
