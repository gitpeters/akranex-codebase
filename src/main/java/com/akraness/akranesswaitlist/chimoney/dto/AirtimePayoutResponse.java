package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirtimePayoutResponse {
    private String amount;
    private String phoneNumber;
    private String status;
    private String errorMessage;
}
