package com.akraness.akranesswaitlist.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WaitListRequestDto {
    @NotBlank(message = "{email.required}")
    private String email;
}
