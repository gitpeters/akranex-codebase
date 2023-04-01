package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FundAccountRequestDto {
    private String receiver;
    private double amount;
    private String wallet;
}
