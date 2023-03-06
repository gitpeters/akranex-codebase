package com.akraness.akranesswaitlist.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerificationDto {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "verification code is required.")
    private String code;
    private boolean verified = false;
}
