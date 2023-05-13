package com.akraness.akranesswaitlist.fundwallet.service;

import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface FundWalletService {
    ResponseEntity<?> fundWallet(FundWalletRequest request, String akranexTag) throws JsonProcessingException;
}
