package com.akraness.akranesswaitlist.identitypass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityPassRequestPayload {
    private String number;
    private String first_name;
    private String dob;
    private String last_name;
    private String state;
    private String number_nin;
    private String firstname;
    private String lastname;
    private String nationalid;
    private String type;
    private String customer_reference;
    private String customer_name;
    private String reservation_number;
    private String reg_number;
    private String countryCode;
}
