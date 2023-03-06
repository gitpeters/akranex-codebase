package com.akraness.akranesswaitlist.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MagicPinRequestDto {
    @NotNull(message = "magic pin cannot be empty")
    private String magicPin;
    @NotNull(message = "email cannot be empty")
    private String email;
}
