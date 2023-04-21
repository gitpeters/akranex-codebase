package com.akraness.akranesswaitlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequestDto {
    @NotBlank(message = "Country code is required.")
    private String countryCode;
    @NotBlank(message = "Email is required.")
    private String email;
}
