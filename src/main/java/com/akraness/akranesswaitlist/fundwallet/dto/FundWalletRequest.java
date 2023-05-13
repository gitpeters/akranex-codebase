package com.akraness.akranesswaitlist.fundwallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundWalletRequest {
    private double amount;
    private String description;
    private String currencyCode;
}
