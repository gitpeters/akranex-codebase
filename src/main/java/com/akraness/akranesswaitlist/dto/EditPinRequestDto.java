package com.akraness.akranesswaitlist.dto;

import com.akraness.akranesswaitlist.enums.PinType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPinRequestDto {
    @NotBlank(message = "Old pin is required.")
    private String oldPin;
    @NotBlank(message = "email is required.")
    private String email;
    @NotBlank(message = "New pin is required.")
    private String newPin;
    @NotBlank(message = "New pin confirm is required.")
    private String newPinConfirm;
    private PinType pinType;
}
