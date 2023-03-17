package com.akraness.akranesswaitlist.identitypass.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityPassRequestNumber {
    private String number;
    private String number_nin;
}
