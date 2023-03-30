package com.akraness.akranesswaitlist.async;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.dto.NotificationDto;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.enums.NotificationType;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassDocumentRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.dto.KYCVerification;
import com.akraness.akranesswaitlist.identitypass.entity.DataSupportedCountry;
import com.akraness.akranesswaitlist.identitypass.repository.DataSupportedCountryRepository;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.util.KYCVericationStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Service
@RequiredArgsConstructor
public class IdentityPassAsyncRunner {
    @Value("${myidentitypass.api-key}")
    private String apiKey;
    @Value("${myidentitypass.app-id}")
    private String appId;
    @Value("${myidentitypass.base-url}")
    private String baseUrl;

    @Value("${myidentitypass.data-base-bvn-url}")
    private String dataBaseUrl;

    @Value("${email.kyc.approval-id}")
    private String approvalTemplateId;

    private final IUserRepository userRepository;
    private final RestTemplateService restTemplateService;
    private final DataSupportedCountryRepository dataSupportedCountryRepository;

    private final INotificationService notificationService;
    @Async
    public void processKYCVerification(User user, Map<String, Object> request) throws JsonProcessingException {
        String type = (String) request.get("type");
        String dataType = (String) request.get("dataType");
        ResponseEntity<CustomResponse> response = processVerification(type, request);


        if (response.getStatusCodeValue() == HttpStatus.OK.value()) {

            Map<String, Object> data = getDataBody(dataType, response);

            KYCVerification verification = response.getBody().getVerification();

            if(verification != null && verification.getStatus() != null && verification.getStatus().equalsIgnoreCase(KYCVericationStatus.VERIFIED.name())){

                String fname = data.containsKey("firstname") ? (String)data.get("firstname")
                        : (data.containsKey("first_name") ? (String)data.get("first_name") : (String)data.get("firstName"));
                String lname = data.containsKey("lastname") ? (String)data.get("lastname")
                        : (data.containsKey("last_name") ? (String)data.get("last_name") : (String)data.get("lastName"));

                if((user.getFirstName().equalsIgnoreCase(fname) && user.getLastName().equalsIgnoreCase(lname)) || (user.getLastName().equalsIgnoreCase(fname) && user.getFirstName().equalsIgnoreCase(lname))) {
                    user.setKycStatus(KYCVericationStatus.VERIFIED.name());
                    user.setKycStatusMessage("Successfully verified");
                    userRepository.save(user);

                    //Send verification email
                    sendKyCVerificationMail(user.getEmail());

                    //Send push notification

                }
                else {
                    user.setKycStatus(KYCVericationStatus.FAILED.name());
                    user.setKycStatusMessage("Failed");
                    userRepository.save(user);

                    //send push notification

                }
            }else {
                user.setKycStatus(KYCVericationStatus.FAILED.name());
                user.setKycStatusMessage("Verification failed");
                userRepository.save(user);

                //send push notification
            }

        }else {
            user.setKycStatus(KYCVericationStatus.FAILED.name());
            user.setKycStatusMessage("Verification failed");
            userRepository.save(user);

            //Send push notification
        }

    }

    private void sendKyCVerificationMail(String email) throws JsonProcessingException {
        NotificationDto notificationDto = NotificationDto.builder()
                .recipient(email)
                .subject("KYC verification")
                .type(NotificationType.EMAIL)
                .templateId(approvalTemplateId)
                .build();
        notificationService.sendNotification(notificationDto);
    }

    private Map<String, Object> getDataBody(String dataType, ResponseEntity<CustomResponse> response) {
        Map<String, Object> data = null;
        if(dataType.equalsIgnoreCase("bvn")){
            data = (Map<String, Object>) response.getBody().getBvn_data();
        }else if(dataType.equalsIgnoreCase("nin")) {
            data = (Map<String, Object>) response.getBody().getNin_data();
        }else if(dataType.equalsIgnoreCase("voters_card")){
            data = (Map<String, Object>) response.getBody().getVc_data();
        }else if(dataType.equalsIgnoreCase("drivers_license")){
            data = (Map<String, Object>) response.getBody().getDriver_license();
        }
        else{
            data = (Map<String, Object>) response.getBody().getData();
        }

        return data;
    }

    private ResponseEntity<CustomResponse> processVerification(String type, Map<String, Object> request) {
        ResponseEntity<CustomResponse> response = null;
        if (type.equalsIgnoreCase("document")) {
            response = ProcessDocumentVerification(request);

        } else {
            response = processDataVerification(request);
        }

        return response;
    }

    public ResponseEntity<CustomResponse> ProcessDocumentVerification(Map<String, Object> request) {
        String url = dataBaseUrl + "document";
        IdentityPassDocumentRequestPayload paylod = IdentityPassDocumentRequestPayload.builder()
                .doc_country((String) request.get("doc_country"))
                .doc_type((String) request.get("doc_type"))
                .doc_image((String) request.get("doc_image"))
                .build();

        ResponseEntity<CustomResponse> response = restTemplateService.post(url, paylod, headers());
        return ResponseEntity.ok().body(response.getBody());
    }

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
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.set("APP-ID", appId);

        return headers;
    }

    public void setCountryPayload(Map<String, Object> payload) throws JsonProcessingException {
        String countryCode = (String) payload.get("countryCode");

       Optional<DataSupportedCountry> supportedCountry =  dataSupportedCountryRepository.findByCountryCode(countryCode);
       if(supportedCountry.isPresent()) {
           dataSupportedCountryRepository.delete(supportedCountry.get());
       }

        String obj = new ObjectMapper().writeValueAsString(payload);

       DataSupportedCountry dsc = new DataSupportedCountry();
       dsc.setCountryCode(countryCode);
       dsc.setPayload(obj);

       dataSupportedCountryRepository.save(dsc);
    }
}
