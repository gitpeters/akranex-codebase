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
public class TransactionHistoryResponse {
    private String referenceId;
    private String transactionType;
    private String transactionDate;
    private BankTransferHistoryDto bankTransfer;
    private AirtimePurhcaseHistoryDto airtimePurchase;
    private ExchangeHistoryDto exchange;
    private double amount;
    private String transactionStatus;
    private String statusMessage;

    public TransactionHistoryResponse(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
