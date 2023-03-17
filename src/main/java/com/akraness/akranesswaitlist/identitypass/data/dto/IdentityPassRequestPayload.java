package com.akraness.akranesswaitlist.identitypass.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityPassRequestPayload {
    private String number;
    private String first_name;
    private String dob;
    private String last_name;
    private String state;
    private String number_nin;
}
