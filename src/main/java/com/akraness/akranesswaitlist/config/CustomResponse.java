package com.akraness.akranesswaitlist.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

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
}
