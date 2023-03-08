package com.akraness.akranesswaitlist.chimoney.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class SubAccountRequest {
    private Integer userId;
    @NotEmpty(message = "name is required")
    private String name;
    @NotEmpty(message = "email is required")
    private String email;
}
