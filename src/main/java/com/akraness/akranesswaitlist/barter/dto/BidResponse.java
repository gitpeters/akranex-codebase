package com.akraness.akranesswaitlist.barter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidResponse {
    private double bidAmount;
    private double receivingAmount;
    private Long offerId;
    private double rate;
    private String bidCurrency;
    private String receivingCurrency;
    private String akranexTag;
    private String bidStatus;
    private String bidMessage;

    public BidResponse(String bidStatus, String bidMessage) {
        this.bidStatus = bidStatus;
        this.bidMessage = bidMessage;
    }
}
