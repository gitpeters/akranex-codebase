package com.akraness.akranesswaitlist.fundwallet.service;

import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;

public interface FundWalletService {
    ResponseEntity<?> fundWallet(FundWalletRequest request, String akranexTag) throws JsonProcessingException, ExecutionException, InterruptedException, FirebaseMessagingException;
}
