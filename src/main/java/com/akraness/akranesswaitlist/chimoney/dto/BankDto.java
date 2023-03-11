package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

@Data
public class BankDto {
    private String countryToSend;
    private String account_bank;
    private String account_number;
    private Integer valueInUSD;
    private String reference;
    private String fullname;
}
