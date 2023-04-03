package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.CurrencyConversionResponse;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyConverterService {
    private final RestTemplateService restTemplateService;
    private final ObjectMapper objectMapper;
    @Value("${chimoney.api-key}")
    private String apiKey;

    @Value("${chimoney.base-url}")
    private String baseUrl;

    public CurrencyConversionResponse convertCurrency(String fromCurrencyCode, String toCurrencyCode, double amount) throws JsonProcessingException {
        CurrencyConversionResponse response = new CurrencyConversionResponse();

        Currency fromCurrency = Currency.getInstance(fromCurrencyCode);
        Currency toCurrency = Currency.getInstance(toCurrencyCode);

        double convertedAmount;
        double rate;

        if(fromCurrencyCode.equals("USD")) {
            convertedAmount =  convertToUSD(toCurrency, amount);
        }else if(toCurrencyCode.equals("USD")){
            convertedAmount =  convertFromUSD(fromCurrency, amount);
        }else {
            double usdAmount = convertToUSD(fromCurrency, amount);
            convertedAmount = convertFromUSD(toCurrency, usdAmount);
        }
        rate = convertedAmount/amount;

        response.setFromCurrency(fromCurrency.getCurrencyCode());
        response.setToCurrency(toCurrency.getCurrencyCode());
        response.setAmount(amount);
        response.setConvertedAmount(convertedAmount);
        response.setRate(rate);

        return response;
    }

    public double convertToUSD(Currency fromCurrency, double amount) throws JsonProcessingException {
        if (amount <= 0) {
            return 0;
        }

        String url = baseUrl + "info/local-amount-in-usd?originCurrency=" + fromCurrency.getCurrencyCode() + "&amountInOriginCurrency=" + amount;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, headers());


        if (response.getStatusCodeValue() == HttpStatus.OK.value()) {
          Map<String, Object> data = objectMapper.convertValue(response.getBody().getData(), Map.class);
           String convertedAmount = String.valueOf(data.get("amountInUSD"));
            return Double.parseDouble(convertedAmount);
        } else {
            throw new RuntimeException("Failed to convert to USD");
        }
    }

    public double convertFromUSD(Currency toCurrency, double amount) throws JsonProcessingException {
        if (amount <= 0) {
            return 0;
        }

        String url = baseUrl + "info/usd-amount-in-local?destinationCurrency=" + toCurrency.getCurrencyCode() + "&amountInUSD=" + amount;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, headers());

        if (response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, Object> data = objectMapper.convertValue(response.getBody().getData(), Map.class);
            String convertedAmount = String.valueOf(data.get("amountInDestinationCurrency"));
            return Double.parseDouble(convertedAmount);
        } else {
            throw new RuntimeException("Failed to convert from USD");
        }
    }
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
