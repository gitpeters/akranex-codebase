package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SubAccountRequestDto {
    private Integer userId;
    private String akranexTag;
    private String email;
    private String countryCode;
}
