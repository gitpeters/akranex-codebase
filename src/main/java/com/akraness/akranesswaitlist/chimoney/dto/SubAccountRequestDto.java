package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class SubAccountRequestDto {
    private Long userId;
    private String akranexTag;
    private String countryCode;
}
