package com.akraness.akranesswaitlist.fundwallet.controller;

import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import com.akraness.akranesswaitlist.fundwallet.service.FundWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mono")
@RequiredArgsConstructor
public class FundWalletController {

    private final FundWalletService fundWalletService;

    @PostMapping("/fund-wallet")

    public ResponseEntity<?> fundWallet(@RequestBody FundWalletRequest request){
        return fundWalletService.fundWallet(request);
    }

}
