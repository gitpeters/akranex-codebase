package com.akraness.akranesswaitlist.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResendPhoneOtpRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
