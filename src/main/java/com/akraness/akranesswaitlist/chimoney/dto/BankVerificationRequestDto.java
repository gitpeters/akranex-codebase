package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

@Data
public class BankVerificationRequestDto {
    private String countryCode;
    private String account_bank;
    private String account_number;
}
