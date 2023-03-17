package com.akraness.akranesswaitlist.config;

import com.akraness.akranesswaitlist.identitypass.data.dto.BVNDataDto;
import com.akraness.akranesswaitlist.identitypass.data.dto.DLDataDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
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
    private BVNDataDto bvn_data;
    private DLDataDto frsc_data;
    private Object verification;
    private String message;
}
