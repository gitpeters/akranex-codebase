package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

@Data
public class RedeemData {
    private Integer productId;
    private String countryCode;
    private Integer valueInLocalCurrency;
}
