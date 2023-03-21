package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceDto {
    private double amount;
    private double amountInLocalCurrency;
}
