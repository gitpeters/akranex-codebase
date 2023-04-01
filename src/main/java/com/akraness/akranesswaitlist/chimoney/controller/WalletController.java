package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.dto.FundAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.service.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chimoney/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/fund-account")
    public ResponseEntity<?> fundAccount(@RequestBody FundAccountRequestDto fundAccountRequestDto) throws JsonProcessingException {
        return walletService.fundAccount(fundAccountRequestDto);
    }
}
