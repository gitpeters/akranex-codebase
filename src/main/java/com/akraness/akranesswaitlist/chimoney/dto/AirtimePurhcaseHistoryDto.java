package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirtimePurhcaseHistoryDto {
    private String transactionStatus;
    private String bankName;
    private String accountNumber;
    private String currencyCode;
    private String accountName;
    private AirtimePayoutResponse payout;
}
