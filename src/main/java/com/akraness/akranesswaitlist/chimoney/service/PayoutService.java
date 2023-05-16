package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.PayoutDto;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;

public interface PayoutService {
    ResponseEntity<CustomResponse> payoutAirtime(PayoutDto request) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    ResponseEntity<CustomResponse> payoutBank(PayoutDto request) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    ResponseEntity<CustomResponse> payoutGiftCard(PayoutDto request) throws ExecutionException, InterruptedException, FirebaseMessagingException;
}
