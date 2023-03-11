package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SubAccountRequestDto {
    private Integer userId;
    @NotEmpty(message = "name is required")
    private String name;
    @NotEmpty(message = "email is required")
    private String email;
}
