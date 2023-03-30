package com.akraness.akranesswaitlist.barter.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferRequest {
    private double amountToBePaid;
    private double amountToBeReceived;
    private double rate;
    private String tradingCurrency;
    private String receivingCurrency;
    private String akranexTag;
}
