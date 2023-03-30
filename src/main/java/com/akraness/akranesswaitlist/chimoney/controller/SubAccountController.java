package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.dto.SubAccountDto;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.dto.TransferDto;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chimoney/subaccout")
@RequiredArgsConstructor
public class SubAccountController {
    private final SubAccountService subAccountService;

    @PostMapping("/create")
    public ResponseEntity<?> createSubAccount(@RequestBody SubAccountRequestDto request ) {
        return subAccountService.createSubAccount(request);
    }

//    @GetMapping("/get")
//    public ResponseEntity<?> getSubAccount(@RequestParam("subAccountId") String subAccountId) throws JsonProcessingException {
//        BalanceDto balanceDto = subAccountService.getSubAccount(subAccountId);
//
//        return ResponseEntity.ok().body(CustomResponse.builder().data(balanceDto).build());
//    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSubAccount(@RequestParam("subAccountId") String subAccountId) {
        return subAccountService.deleteSubAccount(subAccountId);
    }

    @GetMapping("/get-balance-in-local-currency")
    public ResponseEntity<?> getBalanceInLocalCurrency(@RequestParam("amount") double amount, @RequestParam("countryCode") String countryCode) throws JsonProcessingException {
        String currencyCode = subAccountService.getCurrencyCode(countryCode);
        if(currencyCode == null) {
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("No currency code found for the country code "+ countryCode).build());
        }

        double amountInLocal = subAccountService.getAmountInLocalCurrency(amount, currencyCode);
        Map<String, Double> data = new HashMap<>();
        data.put("amount", amountInLocal);
        return ResponseEntity.ok().body(CustomResponse.builder().data(data).build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDto transferDto) throws JsonProcessingException {
        return subAccountService.transfer(transferDto);
    }

    @GetMapping("/get-user-balances")
    public ResponseEntity<?> getUserSubAccountsAndBalance(@RequestParam("userId") Long userId) throws JsonProcessingException {
        List<SubAccountDto> subAccounts = subAccountService.getUserSubAccountsAndBalance(userId);

        return ResponseEntity.ok().body(CustomResponse.builder().data(subAccounts).build());
    }


}
