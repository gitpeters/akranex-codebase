package com.akraness.akranesswaitlist.barter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyConvertRequest {
    String destinationCurrency;
    double amountInUSD;
    double amountInDestinationCurrency;
    double rate;
}
