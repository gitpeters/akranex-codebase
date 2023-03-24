package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.akraness.akranesswaitlist.chimoney.dto.TransferDto;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface SubAccountService {
    ResponseEntity<CustomResponse> createSubAccount(SubAccountRequestDto request);
    BalanceDto getSubAccount(String subAccountId) throws JsonProcessingException;
    ResponseEntity<CustomResponse> deleteSubAccount(String subAccountId);

    BalanceDto getBalanceInLocalCurrency(String subAccountId, String currencyCode) throws JsonProcessingException;

    ResponseEntity<?> transfer(TransferDto transferDto) throws JsonProcessingException;
}
