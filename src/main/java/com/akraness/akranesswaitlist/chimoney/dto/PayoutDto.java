package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayoutDto {
    private String subAccount;
    private boolean turnOffNotification;
    private List<AirtimeDto> airtimes;
    private List<BankDto> banks;
    private List<GiftCardDto> giftcards;
}
