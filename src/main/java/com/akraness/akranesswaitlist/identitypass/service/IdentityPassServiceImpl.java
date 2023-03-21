package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequest;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.KYCVerification;
import com.akraness.akranesswaitlist.identitypass.repository.IdentityPassRepo;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.util.KYCVericationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.lang.invoke.SwitchPoint;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityPassServiceImpl implements IdentityPassService {
    private final RestTemplateService restTemplateService;

    @Value("${myidentitypass.api-key}")
    private String apiKey;
    @Value("${myidentitypass.app-id}")
    private String appId;
    @Value("${myidentitypass.base-url}")
    private String baseUrl;

    @Value("${myidentitypass.data-base-bvn-url}")
    private String dataBaseUrl;

    private final IUserRepository userRepository;

    @Override
    public ResponseEntity<CustomResponse> validateRequest(Map<String, Object> request) {
        String type = (String) request.get("type");
        String dataType = (String) request.get("dataType");

        Optional <User> userObj = userRepository.findById(1l);
        if(!userObj.isPresent()) {
            //return user not found
            return ResponseEntity.badRequest().body(new CustomResponse("Failed", "User not found"));
        }
        User user = userObj.get();
        user.setKycStatus(KYCVericationStatus.PENDING.name());
        userRepository.save(user);

        //background process with Kafka
        ResponseEntity<CustomResponse> response = null;

        if (type.equalsIgnoreCase("document")) {
            response = validateDocument(request);

        } else {
            response = processDataVerification(request);
        }

        if (response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, Object> data = null;
            if(dataType.equalsIgnoreCase("bvn")){
                data = (Map<String, Object>) response.getBody().getBvn_data();
            }else if(dataType.equalsIgnoreCase("nin")) {
                //TODO: for other data type
            }
            else{
                data = (Map<String, Object>) response.getBody().getData();
            }

            KYCVerification verification = response.getBody().getVerification();

            if(verification.getStatus().equalsIgnoreCase(KYCVericationStatus.VERIFIED.name())){

                String fname = data.containsKey("firstname") ? (String)data.get("firstname")
                        : (data.containsKey("first_name") ? (String)data.get("first_name") : (String)data.get("firstName"));
                String lname = data.containsKey("lastname") ? (String)data.get("lastname")
                        : (data.containsKey("last_name") ? (String)data.get("last_name") : (String)data.get("lastName"));

                if((user.getFirstName().equalsIgnoreCase(fname) && user.getLastName().equalsIgnoreCase(lname)) || (user.getLastName().equalsIgnoreCase(fname) && user.getFirstName().equalsIgnoreCase(lname))) {
                    user.setKycStatus(KYCVericationStatus.VERIFIED.name());
                    user.setKycStatusMessage("Successfully verified");
                }
                else {
                    user.setKycStatus(KYCVericationStatus.FAILED.name());
                    user.setKycStatusMessage("Failed");
                }
                userRepository.save(user);
            }else {
                user.setKycStatus(KYCVericationStatus.FAILED.name());
                user.setKycStatusMessage("Verification failed");
                userRepository.save(user);
            }

        }else {
            user.setKycStatus(KYCVericationStatus.FAILED.name());
            user.setKycStatusMessage("Something went wrong");
            userRepository.save(user);
        }

        return ResponseEntity.ok().body(response.getBody());

    }



    // document validation
    @Override
    public ResponseEntity<CustomResponse> validateDocument(Map<String, Object> request) {
        String url = dataBaseUrl + "document";
        IdentityPassDocumentRequestPayload paylod = IdentityPassDocumentRequestPayload.builder()
                .doc_country((String) request.get("doc_country"))
                .doc_type((String) request.get("doc_type"))
                .doc_image((String) request.get("doc_image"))
                .build();

        ResponseEntity<CustomResponse> response = restTemplateService.post(url, paylod, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    // data validation

    @Override
    public ResponseEntity<CustomResponse> processDataVerification(Map<String, Object> request) {
        String countryCode = (String) request.get("countryCode");
        ResponseEntity<CustomResponse> response = null;
        switch(countryCode) {
            case "NG":
                response = validateNigeriaData(request);
                break;
            case "UG":
                response = validateUgandaData(request);
                break;
            case "GH":
                response = validateGhana(request);
                break;
            case "KE":
                response = validateKenya(request);
                break;
            case "ZA":
                response = validateSouthAfrica(request);
                break;
            case "SL":
                response = validateSierraLeone(request);
            default:
                break;
        }
        return response;
    }

    private ResponseEntity<CustomResponse> validateSierraLeone(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), (String)request.get("dataType"));
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        if(dataType.equalsIgnoreCase("voters_card")){
            url += "sl/voters";
        }
        if(dataType.equalsIgnoreCase("drivers_license")){
            url += "sl/drivers_license";
        }
        return restTemplateService.post(url, setPayload((Map<String, Object>) request.get("data")), headers());
    }

    private ResponseEntity<CustomResponse> validateSouthAfrica(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), (String)request.get("dataType"));
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        if(dataType.equalsIgnoreCase("national_id")){
            url += "sa/national_id";
        }
        if(dataType.equalsIgnoreCase("company")){
            url += "sa/company";
        }
        return restTemplateService.post(url, setPayload((Map<String, Object>) request.get("data")), headers());
    }
    private ResponseEntity<CustomResponse> validateKenya(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), (String)request.get("dataType"));
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        if(dataType.equalsIgnoreCase("passport")){
            url += "ke/passportK";
        }
        if(dataType.equalsIgnoreCase("drivers_license")){
            url += "ke/drivers_licenseK";
        }
        if(dataType.equalsIgnoreCase("national_identity")){
            url += "ke/national_id";
        }
        return restTemplateService.post(url, setPayload((Map<String, Object>) request.get("data")), headers());
    }

    private ResponseEntity<CustomResponse> validateGhana(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), (String)request.get("dataType"));
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        if(dataType.equalsIgnoreCase("passport")){
            url += "gh/passport";
        }
        if(dataType.equalsIgnoreCase("drivers_license")){
            url += "gh/drivers_license";
        }
        if(dataType.equalsIgnoreCase("voters_card")){
            url += "gh/voters";
        }
        return restTemplateService.post(url, setPayload((Map<String, Object>) request.get("data")), headers());
    }

    private ResponseEntity<CustomResponse> validateUgandaData(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), (String)request.get("dataType"));
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        if(dataType.equalsIgnoreCase("company")){
            url += "ug/company";
        }
        return restTemplateService.post(url, setPayload((Map<String, Object>) request.get("data")), headers());
    }

    private ResponseEntity<CustomResponse> validateNigeriaData(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), (String)request.get("dataType"));
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        //Object data = request.get("data");


        if(dataType.equalsIgnoreCase("nin")) {
            url += "nin_wo_face";
        }
        if(dataType.equalsIgnoreCase("bvn")) {
            url += "bvn";
        }
        if(dataType.equalsIgnoreCase("voters_card")) {
            url += "v1/biometrics/merchant/data/verification/voters_card";
        }
        if(dataType.equalsIgnoreCase("national_passport")) {
            url += "national_passport";
        }
        if(dataType.equalsIgnoreCase("driver_license")) {
            url += "drivers_license/basic";
        }
        return restTemplateService.post(url, setPayload((Map<String, Object>) request.get("data")), headers());
    }

    private ResponseEntity<?> validateDataRequest(String countryCode, String dataType) {
        List<String> nigeriaSupportedData =  Arrays.asList("bvn", "national_id",
                "national_passport", "nin", "passport", "company",
                "drivers_license", "voters_card",
                "national_identity");
        List<String> kenyaSupportedData =  Arrays.asList("passport", "drivers_license","national_identity");

        List<String> ghanaSupportedData =  Arrays.asList("passport", "drivers_license","voters_card");

        List<String> ugandaSupportedData =  Arrays.asList("company");

        List<String> south_africaSupportedData =  Arrays.asList("company", "national_id");

        List<String> sierra_leoneSupportedData =  Arrays.asList("voters_card", "drivers_license");

        if(countryCode.equalsIgnoreCase("NG") && !nigeriaSupportedData.contains(dataType)) {
            return ResponseEntity.badRequest().body(new CustomResponse("failed", "Invalid data type for Nigeria"));
        }else if(countryCode.equalsIgnoreCase("KE") && kenyaSupportedData.contains(dataType)){
            return ResponseEntity.badRequest().body(new CustomResponse("failed", "Invalid data type for Kenya"));
        }else if(countryCode.equalsIgnoreCase("GH") && ghanaSupportedData.contains(dataType)){
            return ResponseEntity.badRequest().body(new CustomResponse("failed", "Invalid data type for Ghana"));
        }else if(countryCode.equalsIgnoreCase("ZA") && south_africaSupportedData.contains(dataType)) {
            return ResponseEntity.badRequest().body(new CustomResponse("failed", "Invalid data type for South Africa"));
        }else if(countryCode.equalsIgnoreCase("UG") && ugandaSupportedData.contains(dataType)) {
            return ResponseEntity.badRequest().body(new CustomResponse("failed", "Invalid data type for Uganda"));
        }else if(countryCode.equalsIgnoreCase("SL") && sierra_leoneSupportedData.contains(dataType)) {
            return ResponseEntity.badRequest().body(new CustomResponse("failed", "Invalid data type for Sierra Leone"));
        }
        return null;
    }
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.set("APP-ID", appId);

        return headers;
    }

    private IdentityPassRequestPayload setPayload(Map<String, Object> request){
        IdentityPassRequestPayload payload = IdentityPassRequestPayload.builder()
                .first_name((String) request.get("first_name"))
                .last_name((String) request.get("last_name"))
                .customer_name((String) request.get("customer_name"))
                .dob((String) request.get("dob"))
                .nationalid((String) request.get("national_id"))
                .customer_reference((String) request.get("customer_reference"))
                .firstname((String) request.get("firstname"))
                .lastname((String) request.get("lastname"))
                .number((String) request.get("number"))
                .number_nin((String) request.get("number_nin"))
                .reg_number((String) request.get("reg_number"))
                .reservation_number((String) request.get("reservation_number"))
                .state((String) request.get("state"))
                .type((String) request.get("type"))
                .build();
        return payload;
    }
}
