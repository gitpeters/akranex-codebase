package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.dto.EmailVerificationDto;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.storage.model.ObjectAccessControl;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SubAccountServiceImpl implements SubAccountService {
    private final RestTemplateService restTemplateService;
    private final ISubAccountRepository subAccountRepository;
    private final Utility utility;
    @Value("${chimoney.api-key}")
    private String apiKey;

    @Value("${chimoney.base-url}")
    private String baseUrl;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseEntity<CustomResponse> createSubAccount(SubAccountRequestDto request) {
        if (utility.isNullOrEmpty(request.getEmail()))
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("email is required.").build());

        if (utility.isNullOrEmpty(request.getAkranexTag()))
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("akranexTag is required.").build());

        if (utility.isNullOrEmpty(request.getCountryCode()))
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("countryCode is required.").build());

        if (request.getUserId() == null)
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("userId is required.").build());

        Optional<SubAccount> subAccount = subAccountRepository.findByUserIdAndCountryCode(request.getUserId(), request.getCountryCode());
        if(subAccount.isPresent())
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("You already have sub account created for this region").build());

        Map<String, String> req = new HashMap<>();
        req.put("name", request.getAkranexTag());
        req.put("email", request.getEmail());

        String url = baseUrl + "sub-account/create";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, req, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()){
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, String> map = oMapper.convertValue(response.getBody().getData(), Map.class);
            SubAccount subacct = SubAccount.builder()
                    .subAccountId(map.get("id"))
                    .uid(map.get("uid"))
                    .userId(request.getUserId())
                    .countryCode(request.getCountryCode())
                    .build();

            subAccountRepository.save(subacct);
        }



        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<?> getSubAccount(String subAccountId, String countryCode) throws JsonProcessingException {
        String url = baseUrl + "sub-account/get?id="+subAccountId;
        BalanceDto balance = null;
        ObjectMapper oMapper = new ObjectMapper();

        String subAccountData = redisTemplate.opsForValue().get(subAccountId);
        if (subAccountData != null) {
            balance = oMapper.readValue(subAccountData, BalanceDto.class);

            double amountInLocalCurrency = getBalanceInLocalCurrency(balance.getAmount(), countryCode);
            balance.setAmountInLocalCurrency(amountInLocalCurrency);

            return ResponseEntity.ok().body(balance);
        }

        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {

            Map<String, Object> map = oMapper.convertValue(response.getBody().getData(), Map.class);
            List<Object> wallets = (List<Object>) map.get("wallets");

            for(Object walletObj: wallets) {
                Map<String, Object> walletData = oMapper.convertValue(walletObj, Map.class);
                String walletType = (String) walletData.get("type");

                if(!walletType.equalsIgnoreCase("chi")) continue;

                List transactions = (List) walletData.get("transactions");

                Map<String, Object> transMap = oMapper.convertValue(transactions.get(0), Map.class);

                String stringToConvert = String.valueOf(transMap.get("amount"));
                Double amount = Double.parseDouble(stringToConvert);

                balance = BalanceDto.builder()
                        .subAccountId(subAccountId)
                        .amount(amount)
                        .build();

                redisTemplate.opsForValue().set(subAccountId, oMapper.writeValueAsString(balance));

                double amountInLocalCurrency = getBalanceInLocalCurrency(balance.getAmount(), countryCode);
                balance.setAmountInLocalCurrency(amountInLocalCurrency);

            }
        }

        return ResponseEntity.ok().body(balance);

    }

    private double getBalanceInLocalCurrency(double amount, String countryCode) {
        String url = baseUrl + "info/usd-amount-in-local?destinationCurrency="+countryCode+"&amountInUSD="+amount;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        return 0.0;
    }

    @Override
    public ResponseEntity<CustomResponse> deleteSubAccount(String subAccountId) {
        String url = baseUrl + "sub-account/delete?id="+subAccountId;

        ResponseEntity<CustomResponse> response = restTemplateService.delete(url, this.headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
