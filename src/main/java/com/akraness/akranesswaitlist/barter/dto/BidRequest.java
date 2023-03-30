package com.akraness.akranesswaitlist.barter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidRequest {
    private double bidAmount;
    private double receivingAmount;
    private double rate;
    private String akranexTag;
}
