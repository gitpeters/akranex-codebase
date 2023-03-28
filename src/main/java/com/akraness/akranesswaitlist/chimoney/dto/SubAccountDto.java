package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

@Data
public class SubAccountDto {
    private String subAccountId;
    private String uid;
    private Long userId;
    private String countryCode;
    private BalanceDto balance;
}
