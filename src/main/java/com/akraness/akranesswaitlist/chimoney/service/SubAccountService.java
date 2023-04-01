package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.*;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface SubAccountService {
    ResponseEntity<CustomResponse> createSubAccount(SubAccountRequestDto request);
    BalanceDto getSubAccount(String subAccountId, String currencyCode) throws JsonProcessingException;
    ResponseEntity<CustomResponse> deleteSubAccount(String subAccountId);

    //BalanceDto getBalanceInLocalCurrency(String subAccountId, String currencyCode) throws JsonProcessingException;

    ResponseEntity<?> transfer(TransferDto transferDto) throws JsonProcessingException;
    List<SubAccount> getUserSubAccounts(Long userId);
   // List<BalanceDto> getUserBalances(List<SubAccount> subAccountList);
    List<SubAccountDto> getUserSubAccountsAndBalance(Long userId) throws JsonProcessingException;
    double getAmountInLocalCurrency(double amount, String currencyCode) throws JsonProcessingException;
    String getCurrencyCode(String countryCode);
}
