package com.akraness.akranesswaitlist.chimoney.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferDto {
    private String senderSubAccountId;
    private String receiverSubAccountId;
    private String akranexTag;
    private String amount;
    private String walletType;
}
