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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundWalletResponse {
    private double amount;
    private String reference;
    private String status;
    private String description;
    private String transactionDate;
    private String message;

    public FundWalletResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
