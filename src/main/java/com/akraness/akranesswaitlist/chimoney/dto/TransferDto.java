package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
    private String senderSubAccountId;
    private String receiverSubAccountId;
    private String akranexTag;
    private double amount;
    private String walletType;
}
