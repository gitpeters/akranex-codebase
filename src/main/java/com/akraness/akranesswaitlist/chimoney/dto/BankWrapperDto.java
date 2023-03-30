package com.akraness.akranesswaitlist.chimoney.dto;

import lombok.Data;

import java.util.List;

@Data
public class BankWrapperDto {
    List<BankVerificationRequestDto> verifyAccountNumbers;
}
