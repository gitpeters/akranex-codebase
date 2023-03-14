package com.akraness.akranesswaitlist.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class PasswordResetRequestDto {
    @NotBlank(message = "Email is required.")
    private String email;
}
