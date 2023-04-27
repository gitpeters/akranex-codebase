package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubAccountRequestDto {
    private Long userId;
    private String akranexTag;
    private String countryCode;
}
