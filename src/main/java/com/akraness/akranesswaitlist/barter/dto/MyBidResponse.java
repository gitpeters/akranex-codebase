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
public class MyBidResponse {
    private Long bidId;
    private double askedAmount;
    private double bidAmount;
    private double rate;
    private String status;
}
