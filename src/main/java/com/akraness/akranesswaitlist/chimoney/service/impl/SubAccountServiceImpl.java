package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.async.AsyncRunner;
import com.akraness.akranesswaitlist.chimoney.dto.*;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.Country;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.repository.ICountryRepository;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.service.IService;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
    private final AsyncRunner asyncRunner;
    @Value("${chimoney.api-key}")
    private String apiKey;

    @Value("${chimoney.base-url}")
    private String baseUrl;

    private final StringRedisTemplate redisTemplate;
    private final ICountryRepository countryRepository;
    private static final String USER_BALANCE = "user_balance";
    private final IUserRepository userRepository;

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

        String currencyCode = getCurrencyCode(request.getCountryCode());
        if (currencyCode == null)
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("There is no currency code associated with this country code.").build());

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
                    .currencyCode(currencyCode)
                    .build();

            subAccountRepository.save(subacct);
        }

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public BalanceDto getSubAccount(String subAccountId, String currencyCode) throws JsonProcessingException {
        String url = baseUrl + "sub-account/get?id="+subAccountId;
        BalanceDto balance = new BalanceDto();
        ObjectMapper oMapper = new ObjectMapper();

        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {

            Map<String, Object> map = oMapper.convertValue(response.getBody().getData(), Map.class);
            List<Object> wallets = (List<Object>) map.get("wallets");

            for(Object walletObj: wallets) {
                Map<String, Object> walletData = oMapper.convertValue(walletObj, Map.class);
                String walletType = (String) walletData.get("type");

                if(!walletType.equalsIgnoreCase("chi")) continue;

                String stringToConvert = String.valueOf(walletData.get("balance"));
                Double amount = Double.parseDouble(stringToConvert);

                //get amount in local currency
                double localAmount = getAmountInLocalCurrency(amount, currencyCode);

                balance = BalanceDto.builder()
                        .subAccountId(subAccountId)
                        .amount(amount)
                        .amountInLocalCurrency(localAmount)
                        .build();
            }
        }

        return balance;

    }

    @Override
    public ResponseEntity<?> transfer(TransferDto transferDto) throws JsonProcessingException {
        Map<String, String> req = new HashMap<>();
        if(!utility.isNullOrEmpty(transferDto.getAkranexTag())) {
            Optional<User> userObj = userRepository.findByAkranexTag(transferDto.getAkranexTag());
            if(!userObj.isPresent()){
                return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("The Akranex Tag: "+transferDto.getAkranexTag() +" is invalid").build());
            }

            User user = userObj.get();
            Optional<SubAccount> subAccountObj = subAccountRepository.findByUserIdAndCountryCode(user.getId(), user.getCountryCode());
            if(!subAccountObj.isPresent()) {
                return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("Primary wallet attached to "+transferDto.getAkranexTag() +" is not found").build());
            }
            req.put("receiver", subAccountObj.get().getSubAccountId());
        }else {
            req.put("receiver", transferDto.getReceiverSubAccountId());
        }

        req.put("subAccount", transferDto.getSenderSubAccountId());
        req.put("amount", transferDto.getAmount());
        req.put("wallet", transferDto.getWalletType());

        String url = baseUrl + "accounts/transfer";

        ResponseEntity<CustomResponse> response = restTemplateService.post(url, req, this.headers());
        if(response.getStatusCodeValue() == HttpStatus.OK.value() && response.getBody().getStatus().equalsIgnoreCase("success")) {
            //remove balance from redis
            asyncRunner.removeBalanceFromRedis(Arrays.asList(transferDto.getSenderSubAccountId(), transferDto.getReceiverSubAccountId()));
        }

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
        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            asyncRunner.removeBalanceFromRedis(Arrays.asList(subAccountId));
        }
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public List<SubAccountDto> getUserSubAccountsAndBalance(Long userId) throws JsonProcessingException {
        List<SubAccount> subAccountList = getUserSubAccounts(userId);
        List<BalanceDto> balanceDtos = getUserBalances(userId, subAccountList);
        List<SubAccountDto> subAccountDtos = new ArrayList<>();

        subAccountList.stream().forEach(s -> {
            Optional<BalanceDto> balanceDto = balanceDtos.stream().filter(b -> b.getSubAccountId().equalsIgnoreCase(s.getSubAccountId()))
                    .findFirst();
            SubAccountDto subAccountDto = SubAccountDto.builder()
                    .subAccountId(s.getSubAccountId())
                    .uid(s.getUid())
                    .userId(s.getUserId())
                    .countryCode(s.getCountryCode())
                    .currencyCode(s.getCurrencyCode())
                    .balance(balanceDto.get())
                    .build();
            subAccountDtos.add(subAccountDto);
        });

        return subAccountDtos;
    }

    @Override
    public double getAmountInLocalCurrency(double amount, String currencyCode) throws JsonProcessingException {
        double amountInLocal = 0L;
        if(amount <= 0) {
            return amountInLocal;
        }

        String url = baseUrl + "info/usd-amount-in-local?destinationCurrency="+currencyCode+"&amountInUSD="+amount;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, Object> data = new ObjectMapper().convertValue(response.getBody().getData(), Map.class);
            String convertedAmount = String.valueOf(data.get("amountInDestinationCurrency"));
            amountInLocal = Double.parseDouble(convertedAmount);
        }

        return amountInLocal;
    }

    @Override
    public String getCurrencyCode(String countryCode) {
        String currencyCode = redisTemplate.opsForValue().get(countryCode);
        if (currencyCode != null) {
            return currencyCode;
        }

        Optional<Country> countryObj = countryRepository.findByCode(countryCode);
        if(countryObj.isPresent()) {
            redisTemplate.opsForValue().set(countryCode, countryObj.get().getCurrencyCode());
            return countryObj.get().getCurrencyCode();
        }

        return null;
    }

    private List<BalanceDto> getUserBalances(long userId, List<SubAccount> subAccountList) throws JsonProcessingException {
        List<BalanceDto> balanceDtos = new ArrayList<>();
        ObjectMapper om = new ObjectMapper();
        String balanceData = redisTemplate.opsForValue().get(userId+USER_BALANCE);
        if(Objects.nonNull(balanceData)) {
           balanceDtos = om.readValue(balanceData, new TypeReference<List<BalanceDto>>(){});
           return balanceDtos;
        }

        List<BalanceDto> finalBalanceDtos = balanceDtos;
        subAccountList.stream().forEach(s -> {
            try {
                finalBalanceDtos.add(getSubAccount(s.getSubAccountId(), s.getCurrencyCode()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        redisTemplate.opsForValue().set(userId+USER_BALANCE, om.writeValueAsString(balanceDtos), Duration.ofDays(5));

        return balanceDtos;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
