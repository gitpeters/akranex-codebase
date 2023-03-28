package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountDto;
import com.akraness.akranesswaitlist.chimoney.dto.TransferDto;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

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

    private final StringRedisTemplate redisTemplate;

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
    public BalanceDto getSubAccount(String subAccountId) throws JsonProcessingException {
        String url = baseUrl + "sub-account/get?id="+subAccountId;
        BalanceDto balance = new BalanceDto();
        ObjectMapper oMapper = new ObjectMapper();

        String subAccountData = redisTemplate.opsForValue().get(subAccountId);
        if (subAccountData != null) {
            balance = oMapper.readValue(subAccountData, BalanceDto.class);

            return balance;
        }

        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {

            Map<String, Object> map = oMapper.convertValue(response.getBody().getData(), Map.class);
            List<Object> wallets = (List<Object>) map.get("wallets");

            for(Object walletObj: wallets) {
                Map<String, Object> walletData = oMapper.convertValue(walletObj, Map.class);
                String walletType = (String) walletData.get("type");

                if(!walletType.equalsIgnoreCase("chi")) continue;

//                List transactions = (List) walletData.get("transactions");
//
//                Map<String, Object> transMap = oMapper.convertValue(transactions.get(0), Map.class);

                String stringToConvert = String.valueOf(walletData.get("balance"));
                Double amount = Double.parseDouble(stringToConvert);

                balance = BalanceDto.builder()
                        .subAccountId(subAccountId)
                        .amount(amount)
                        .build();

                redisTemplate.opsForValue().set(subAccountId, oMapper.writeValueAsString(balance));

            }
        }

        return balance;

    }

    public BalanceDto getBalanceInLocalCurrency(String subAccountId, String currencyCode) throws JsonProcessingException {
        BalanceDto balance = null;
        ObjectMapper oMapper = new ObjectMapper();

        String subAccountData = redisTemplate.opsForValue().get(subAccountId);
        if (subAccountData != null) {
            balance = oMapper.readValue(subAccountData, BalanceDto.class);
        }else {
            balance = getSubAccount(subAccountId);
        }

        if(balance.getAmount() <= 0) {
            return balance;
        }

        String url = baseUrl + "info/usd-amount-in-local?destinationCurrency="+currencyCode+"&amountInUSD="+balance.getAmount();
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, Object> data = oMapper.convertValue(response.getBody().getData(), Map.class);
            String convertedAmount = String.valueOf(data.get("amountInDestinationCurrency"));
            balance.setAmountInLocalCurrency(Double.parseDouble(convertedAmount));
        }

        return balance;
    }

    @Override
    public ResponseEntity<?> transfer(TransferDto transferDto) throws JsonProcessingException {
        Map<String, String> req = new HashMap<>();
        req.put("subAccount", transferDto.getSenderSubAccountId());
        req.put("receiver", transferDto.getReceiverSubAccountId());
        req.put("amount", transferDto.getAmount());
        req.put("wallet", transferDto.getWalletType());

        String url = baseUrl + "accounts/transfer";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, req, this.headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public List<SubAccount> getUserSubAccounts(Long userId) {
        List<SubAccount> subAccountList = subAccountRepository.findByUserId(userId);
        return subAccountList;
    }

    @Override
    public ResponseEntity<CustomResponse> deleteSubAccount(String subAccountId) {
        String url = baseUrl + "sub-account/delete?id="+subAccountId;

        ResponseEntity<CustomResponse> response = restTemplateService.delete(url, this.headers());
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public List<BalanceDto> getUserBalances(List<SubAccount> subAccountList) {
        List<BalanceDto> balanceDtos = new ArrayList<>();

        subAccountList.stream().forEach(s -> {
            try {
                balanceDtos.add(getSubAccount(s.getSubAccountId()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return balanceDtos;
    }

    @Override
    public List<SubAccountDto> getUserSubAccountsAndBalance(Long userId) {
        List<SubAccount> subAccountList = getUserSubAccounts(userId);
        List<BalanceDto> balanceDtos = getUserBalances(subAccountList);
        List<SubAccountDto> subAccountDtos = new ArrayList<>();

        subAccountList.stream().forEach(s -> {
            Optional<BalanceDto> balanceDto = balanceDtos.stream().filter(b -> b.getSubAccountId().equalsIgnoreCase(s.getSubAccountId()))
                    .findFirst();
            SubAccountDto subAccountDto = SubAccountDto.builder()
                    .subAccountId(s.getSubAccountId())
                    .uid(s.getUid())
                    .userId(s.getUserId())
                    .countryCode(s.getCountryCode())
                    .balance(balanceDto.get())
                    .build();
            subAccountDtos.add(subAccountDto);
        });

        return subAccountDtos;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
