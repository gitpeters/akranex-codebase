package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferDto {
    private String senderSubAccountId;
    private String receiverSubAccountId;
    private String amount;
    private String walletType;
}
