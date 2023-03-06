package com.akraness.akranesswaitlist.dto;

import lombok.Data;

@Data
public class TransactionPinRequestDto {
    private String transactionPin;
    private String email;
}
