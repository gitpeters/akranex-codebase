package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

@Data
public class GiftCardDto {
    private String email;
    private String valueInUSD;
    private RedeemData redeemData;
}
