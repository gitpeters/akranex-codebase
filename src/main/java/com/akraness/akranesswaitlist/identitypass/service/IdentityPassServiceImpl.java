package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequest;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
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

    private final IdentityPassRepo identityPassRepo;

    @Value("${myidentitypass.api-key}")
    private String apiKey;
    @Value("${myidentitypass.app-id}")
    private String appId;
    @Value("${myidentitypass.base-url}")
    private String baseUrl;

    @Value("${myidentitypass.data-base-bvn-url}")
    private String dataBaseUrl;

    private final IUserRepository userRepository;

    // Validation for Nigeria
    @Override
    public ResponseEntity<CustomResponse> validateNG_Bvn(IdentityPassRequestPayload  request) {
        String url = dataBaseUrl + "bvn";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        if (request == null) {
            return ResponseEntity.badRequest().body(new CustomResponse("false", "Invalid country code or data type"));
        }
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_Nin(IdentityPassRequestPayload  request) {
        String url = dataBaseUrl + "nin_wo_face";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_VotersCard(IdentityPassRequestPayload request) {
        String url = baseUrl + "v1/biometrics/merchant/data/verification/voters_card";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    // Validation for Kenya
    @Override
    public ResponseEntity<CustomResponse> validateKE_NationalId(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ke/national_id";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateKE_Passport(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ke/passportK";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateKE_DriversLicense(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ke/drivers_licensek";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateGH_IntPassport(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "gh/passport";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateGH_VotersCard(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "gh/voters";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateGH_DriverLicense(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "gh/drivers_license";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateUG_company(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "ug/company";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

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

    @Override
    public ResponseEntity<CustomResponse> validateZA_nationalId(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "sa/national_id";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_IntPassport(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "national_passport";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateNG_DriverLicense(IdentityPassRequestPayload request) {
        String url = dataBaseUrl + "drivers_license/basic";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, request, headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> validateRequest(Map<String, Object> request) {
        String type = (String) request.get("type");
        String countryCode = (String) request.get("countryCode");

        Optional <User> userObj = userRepository.findById((Long) request.get("userId"));
        if(!userObj.isPresent()) {
            //return user not found
        }



        User user = userObj.get();
        user.setKycVerificationStatus(KYCVericationStatus.PENDING.name());
        userRepository.save(user);

        //background process with Kafka
        ResponseEntity<CustomResponse> response = null;

        if(type.equalsIgnoreCase("document")) {
            response = validateDocument(request);

        }else {
            response = processDataVerification(request);
        }

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, Object> data = null;
            Map<String, String> dataObj = (Map<String, String>) response.getBody();
            if(dataObj.containsKey("vc_data")) {
                data = (Map<String, Object>) response.getBody().getVc_data();
            }else if(dataObj.containsKey("nin_data")) {
                data = (Map<String, Object>) response.getBody().getNin_data();
            }else {
                data = (Map<String, Object>) response.getBody().getData();
            }

            String fname = "";
            if(data.containsKey("firstname")) {
                fname = (String) data.get("firstname");
            }
            if(data.containsKey("first_name")) {
                fname = (String) data.get("first_name");
            }

            if((user.getFirstName().equalsIgnoreCase(fname) && user.getLastName() == "") || ) {
                user.setKycVerificationStatus(KYCVericationStatus.VERIFIED.name());
                user.setKycVerificationMessage("Name ");

            }else {
                user.setKycVerificationStatus(KYCVericationStatus.FAILED.name());
                user.setKycVerificationMessage("Name does not match");
            }

            userRepository.save(user);

        }else {
            user.setKycVerificationStatus(KYCVericationStatus.FAILED.name());
            user.setKycVerificationMessage("Something went wrong");
            userRepository.save(user);
        }





        return response;


    }

    private ResponseEntity<CustomResponse> processDataVerification(Map<String, Object> request) {
        String countryCode = (String) request.get("countryCode");
        ResponseEntity<CustomResponse> response = null;
        switch(countryCode) {
            case "NG":
                response = validateNigeriaData(request);
                break;
            case "UG":
                response = validateUgandaData(request);
                break;
            default:

        }

        return response;
    }

    private ResponseEntity<CustomResponse> validateUgandaData(Map<String, Object> request) {
        return null;
    }

    private ResponseEntity<CustomResponse> validateNigeriaData(Map<String, Object> request) {
        validateDataRequest((String) request.get("countryCode"), "");
        String dataType = (String) request.get("dataType");
        String url = dataBaseUrl;
        if(dataType.equalsIgnoreCase("nin")) {
            url += "nin";
        }

        if(dataType.equalsIgnoreCase("bvn")) {
            url += "bvn";
        }

        return restTemplateService.post(url, request.get("data"), headers());
    }

    private ResponseEntity<?> validateDataRequest(String countryCode, String dataType) {
        List<String> nigeria = Arrays.asList("nin", "");
        if(countryCode.equalsIgnoreCase("NG") && !nigeria.contains(dataType)) {
            //return ResponseEntity.badRequest().body(new CustomResponse("false", "Invalid country code or data type"));
        }

        return null;
    }

    // Re-routing the api url based on the country code and data type

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.set("APP-ID", appId);

        return headers;
    }

    public IdentityPassRequestPayload mapToIdentityPassRequestPayload(LinkedHashMap<String, Object> map) {
        IdentityPassRequestPayload payload = new IdentityPassRequestPayload();
        payload.setNumber((String) map.get("number"));
        payload.setFirst_name((String) map.get("first_name"));
        payload.setLast_name((String) map.get("last_name"));
        payload.setState((String) map.get("state"));
        payload.setDob((String) map.get("dob"));
        payload.setNumber_nin((String) map.get("number_nin"));
        payload.setFirstname((String) map.get("firstname"));
        payload.setLastname((String) map.get("lastname"));
        payload.setNationalid((String) map.get("nationalid"));
        payload.setCustomer_name((String) map.get("customer_name"));
        payload.setCustomer_reference((String) map.get("customer_reference"));
        payload.setReservation_number((String) map.get("reservation_number"));
        payload.setReg_number((String) map.get("reg_number"));
        return payload;
    }
}
