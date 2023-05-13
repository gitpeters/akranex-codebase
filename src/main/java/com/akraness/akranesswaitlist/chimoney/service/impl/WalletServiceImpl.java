package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.async.AsyncRunner;
import com.akraness.akranesswaitlist.chimoney.dto.FundAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.service.WalletService;
import com.akraness.akranesswaitlist.chimoney.utils.ChimoneyUtility;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final RestTemplateService restTemplateService;
    private final ChimoneyUtility chimoneyUtility;
    private final AsyncRunner asyncRunner;
    @Override
    public ResponseEntity<?> fundAccount(FundAccountRequestDto fundAccountRequestDto) {
        String url = chimoneyUtility.getBaseUrl() + "wallets/transfer";

        Map<String, Object> mapReq = new HashMap<>();
        mapReq.put("receiver", fundAccountRequestDto.getReceiver());
        mapReq.put("wallet", fundAccountRequestDto.getWallet());
        mapReq.put("valueInUSD", fundAccountRequestDto.getAmount());

        ResponseEntity<CustomResponse> response = restTemplateService.post(url, mapReq, chimoneyUtility.headers());
        if(response.getStatusCodeValue() == HttpStatus.OK.value() && response.getBody().getStatus().equalsIgnoreCase("success")) {
            //remove balance from redis
            //asyncRunner.removeBalanceFromRedis(Arrays.asList(fundAccountRequestDto.getReceiver()));
        }

        return ResponseEntity.ok().body(response.getBody());
    }
}
