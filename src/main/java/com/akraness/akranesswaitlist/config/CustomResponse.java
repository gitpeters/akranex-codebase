package com.akraness.akranesswaitlist.config;

import com.akraness.akranesswaitlist.identitypass.dto.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse {
    //Chimoney error and response
    private String status;
    private String error;
    private Object data;

    //Plaid error and response
    private String error_code;
    private String error_message;
    private String error_type;
    private String id;
    private String request_id;
    private Object steps;
    private List<Object> identity_verifications;

    //IdentityPass
    private Object detail;
    private String response_code;
//    private BVNDataDto bvn_data;
    private Object bvn_data;
    private DLDataDto frsc_data;
    private NINDataDto nin_data;
    private VotersCardDataDto vc_data;
    private KYCVerification verification;
    private DriverLicenseData driver_license;
    private String message;
    public CustomResponse(String status, String error) {
        this.status = status;
        this.error = error;
    }
}
