package com.akraness.akranesswaitlist.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDto {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "verification code is required.")
    private String code;
    private boolean verified = false;
}
