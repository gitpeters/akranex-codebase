package com.akraness.akranesswaitlist.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ResetPasswordDto {
    @NotBlank(message = "Email is required.")
    private String email;
    @NotBlank(message = "Password is required.")
    private String password;
    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;
}
