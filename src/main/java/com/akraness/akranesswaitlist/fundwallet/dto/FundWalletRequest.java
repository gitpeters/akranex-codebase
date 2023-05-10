package com.akraness.akranesswaitlist.fundwallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundWalletRequest {
    private String amount;
    private String type;
    private String description;
    private String reference;
    private String akranexTag;
}
