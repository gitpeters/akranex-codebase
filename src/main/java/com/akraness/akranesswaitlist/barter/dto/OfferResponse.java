package com.akraness.akranesswaitlist.barter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferResponse {
    private double amountToBePaid;
    private double amountToBeReceived;
    private double rate;
    private String tradingCurrency;
    private String receivingCurrency;
    private String akranexTag;
    private String username;
    private List<BidResponse> bids;
    private boolean offerStatus;
    private String offerMessage;

    public OfferResponse(boolean offerStatus, String offerMessage) {
        this.offerStatus = offerStatus;
        this.offerMessage = offerMessage;
    }
}
