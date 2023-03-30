package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.CurrencyConvertRequest;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
    private final RestTemplateService restTemplateService;
    @Value("${chimoney.api-key}")
    private String apiKey;

    @Value("${chimoney.base-url}")
    private String baseUrl;

    public CurrencyConvertRequest getBalanceInLocalCurrency(String currencyCode, double amount) throws JsonProcessingException {
        CurrencyConvertRequest convertRequest = new CurrencyConvertRequest();
        ObjectMapper oMapper = new ObjectMapper();
        convertRequest.setAmountInUSD(amount);
        if(convertRequest.getAmountInUSD()<= 0) {
            return convertRequest;
        }

        String url = baseUrl + "info/usd-amount-in-local?destinationCurrency="+currencyCode+"&amountInUSD="+amount;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, Object> data = oMapper.convertValue(response.getBody().getData(), Map.class);
            String convertedAmount = String.valueOf(data.get("amountInDestinationCurrency"));
            convertRequest.setAmountInDestinationCurrency(Double.parseDouble(convertedAmount));
            convertRequest.setDestinationCurrency((String)data.get("destinationCurrency"));
        }
        if(amount==1){
            Map<String, Object> data = oMapper.convertValue(response.getBody().getData(), Map.class);
            String convertedAmount = String.valueOf(data.get("amountInDestinationCurrency"));
            convertRequest.setRate(Double.parseDouble(convertedAmount));

        }

        return convertRequest;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
