package com.akraness.akranesswaitlist.barter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidResponse {
    private Long bidId;
    private double bidAmount;
    private double receivingAmount;
    private Long offerId;
    private double rate;
    private String bidCurrency;
    private String receivingCurrency;
    private String akranexTag;
    private String bidStatus;
    private String bidMessage;
}
