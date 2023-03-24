package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.dto.TransferDto;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chimoney/subaccout")
@RequiredArgsConstructor
public class SubAccountController {
    private final SubAccountService subAccountService;

    @PostMapping("/create")
    public ResponseEntity<?> createSubAccount(@RequestBody SubAccountRequestDto request ) {
        return subAccountService.createSubAccount(request);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getSubAccount(@RequestParam("subAccountId") String subAccountId) throws JsonProcessingException {
        BalanceDto balanceDto = subAccountService.getSubAccount(subAccountId);

        return ResponseEntity.ok().body(CustomResponse.builder().data(balanceDto).build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSubAccount(@RequestParam("subAccountId") String subAccountId) {
        return subAccountService.deleteSubAccount(subAccountId);
    }

    @GetMapping("/get-balance-local-currency")
    public ResponseEntity<?> getBalanceInLocalCurrency(@RequestParam("subAccountId") String subAccountId, @RequestParam("currencyCode") String currencyCode) throws JsonProcessingException {
        BalanceDto balanceDto = subAccountService.getBalanceInLocalCurrency(subAccountId, currencyCode);

        return ResponseEntity.ok().body(CustomResponse.builder().data(balanceDto).build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDto transferDto) throws JsonProcessingException {
        return subAccountService.transfer(transferDto);
    }
}
