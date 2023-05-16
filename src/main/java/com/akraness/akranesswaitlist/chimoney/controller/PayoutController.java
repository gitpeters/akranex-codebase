package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.dto.PayoutDto;
import com.akraness.akranesswaitlist.chimoney.service.PayoutService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/chimoney/payout")
@RequiredArgsConstructor
public class PayoutController {
    private final PayoutService payoutService;

    @PostMapping("/airtime")
    public ResponseEntity<?> payoutAirtime(@RequestBody PayoutDto payoutDto) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        return payoutService.payoutAirtime(payoutDto);
    }

    @PostMapping("/bank")
    public ResponseEntity<?> payoutBank(@RequestBody PayoutDto payoutDto) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        return payoutService.payoutBank(payoutDto);
    }

    @PostMapping("/gift-card")
    public ResponseEntity<?> payoutGifCard(@RequestBody PayoutDto payoutDto) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        return payoutService.payoutGiftCard(payoutDto);
    }
}
