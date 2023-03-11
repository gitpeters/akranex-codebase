package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.PayoutDto;
import com.akraness.akranesswaitlist.config.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface PayoutService {
    ResponseEntity<CustomResponse> payoutAirtime(PayoutDto request);
    ResponseEntity<CustomResponse> payoutBank(PayoutDto request);
    ResponseEntity<CustomResponse> payoutGiftCard(PayoutDto request);
}
