package com.akraness.akranesswaitlist.dto;

import com.akraness.akranesswaitlist.enums.PinType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VerifyPinRequestDto {
    @NotNull(message = "pin is required")
    private String pin;
    @NotNull(message = "username is required")
    private String username;
    private PinType pinType;
}
