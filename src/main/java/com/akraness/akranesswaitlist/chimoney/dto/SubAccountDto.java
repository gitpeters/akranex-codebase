package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubAccountDto {
    private String subAccountId;
    private String uid;
    private Long userId;
    private String countryCode;
    private String currencyCode;
    private BalanceDto balance;
}
