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
public class BankPayoutResponse {
    private String accountNumber;
    private double amountInLocalCurrency;
    private String bankName;
    private String currency;

}
