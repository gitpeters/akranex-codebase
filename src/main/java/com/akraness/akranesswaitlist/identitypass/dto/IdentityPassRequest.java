package com.akraness.akranesswaitlist.identitypass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityPassRequest {
    private String name;
    private String number;
}
