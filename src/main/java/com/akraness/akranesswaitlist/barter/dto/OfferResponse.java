package com.akraness.akranesswaitlist.barter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferResponse {
    private Long offerId;
    private double amountToBePaid;
    private double amountToBeReceived;
    private double rate;
    private String tradingCurrency;
    private String receivingCurrency;
    private String akranexTag;
    private String username;
    private double transactionFee;
    private List<BidResponse> bids;
    private boolean offerStatus;
    private String offerMessage;

    public OfferResponse(double amountToBePaid, double amountToBeReceived, double rate, String tradingCurrency, String receivingCurrency, double transactionFee, boolean offerStatus, String offerMessage) {
        this.amountToBePaid = amountToBePaid;
        this.amountToBeReceived = amountToBeReceived;
        this.rate = rate;
        this.tradingCurrency = tradingCurrency;
        this.receivingCurrency = receivingCurrency;
        this.transactionFee = transactionFee;
        this.offerStatus = offerStatus;
        this.offerMessage = offerMessage;
    }
}
