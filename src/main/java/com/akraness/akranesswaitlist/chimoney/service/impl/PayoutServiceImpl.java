package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.dto.PayoutDto;
import com.akraness.akranesswaitlist.chimoney.service.PayoutService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {
    private final RestTemplateService restTemplateService;
    private final Utility utility;

    @Value("${chimoney.api-key}")
    private String apiKey;
    @Value("${chimoney.base-url}")
    private String baseUrl;

    @Override
    public ResponseEntity<CustomResponse> payoutAirtime(PayoutDto request) {
        String url = baseUrl + "payouts/airtime";
        return this.post(request, url);
    }

    @Override
    public ResponseEntity<CustomResponse> payoutBank(PayoutDto request) {
        String url = baseUrl + "payouts/bank";
        return this.post(request, url);
    }

    @Override
    public ResponseEntity<CustomResponse> payoutGiftCard(PayoutDto request) {
        String url = baseUrl + "payouts/gift-card";
        return this.post(request, url);
    }

    private ResponseEntity<CustomResponse> post(PayoutDto req, String url) {
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, req, this.headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}