package com.akraness.akranesswaitlist.fundwallet.controller;

import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import com.akraness.akranesswaitlist.fundwallet.service.FundWalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mono")
@RequiredArgsConstructor
public class FundWalletController {

    private final FundWalletService fundWalletService;

    @PostMapping("/fund-wallet")

    public ResponseEntity<?> fundWallet(@RequestBody FundWalletRequest request, @RequestParam String akranexTag) throws JsonProcessingException {
        return fundWalletService.fundWallet(request, akranexTag);
    }

}
