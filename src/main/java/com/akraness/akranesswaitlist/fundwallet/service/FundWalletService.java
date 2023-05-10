package com.akraness.akranesswaitlist.fundwallet.service;

import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import org.springframework.http.ResponseEntity;

public interface FundWalletService {
    ResponseEntity<?> fundWallet(FundWalletRequest request);
}
