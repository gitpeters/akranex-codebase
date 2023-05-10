package com.akraness.akranesswaitlist.fundwallet.service;

import com.akraness.akranesswaitlist.chimoney.dto.FundAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.chimoney.service.WalletService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletResponse;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FundWalletServiceImpl implements FundWalletService{

    private final RestTemplateService restTemplateService;
    private final WalletService walletService;
    private final IUserRepository userRepository;
    private final ISubAccountRepository subAccountRepository;
    @Value("${mono.api-key}")
    private String apiKey;
    @Value("${mono.base-url}")
    private String baseUrl;

    private final Utility utility;

    @Override
    public ResponseEntity<?> fundWallet(FundWalletRequest request) {

        Map<String, String> fundWalletRequest = new HashMap<>();

        if(utility.isNullOrEmpty(request.getAmount())){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "amount is require"));
        }
        if(utility.isNullOrEmpty(request.getAkranexTag())){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "akranexTag is require"));
        }
        if(utility.isNullOrEmpty(request.getReference())){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "reference is require"));
        }
        if(utility.isNullOrEmpty(request.getType())){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "type is require"));
        }
        if(utility.isNullOrEmpty(request.getDescription())){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "description is require"));
        }

        fundWalletRequest.put("amount", request.getAmount());
        fundWalletRequest.put("type", request.getType());
        fundWalletRequest.put("description", request.getDescription());
        fundWalletRequest.put("reference", request.getReference());

        String url = baseUrl+"payments/initiate";

        ResponseEntity<CustomResponse> response = restTemplateService.post(url, fundWalletRequest, headers());
        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, String> verifyPaymentStatus = new HashMap<>();
            verifyPaymentStatus.put("reference", String.valueOf(request.getReference()));
            ResponseEntity<CustomResponse> verifyResponse = restTemplateService.post(url, verifyPaymentStatus, headers());
            if(verifyResponse.getStatusCodeValue() == HttpStatus.OK.value() && verifyResponse.getBody().getStatus().equalsIgnoreCase("successful")){
                fundUserAccount(request);
            }
        }
        return ResponseEntity.ok().body(new FundWalletResponse(String.valueOf(HttpStatus.OK), "Wallet funding was successful"));
    }

    private ResponseEntity<?> fundUserAccount(FundWalletRequest request) {
        Optional<User> userOpt = userRepository.findByAkranexTag(request.getAkranexTag());
        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "No user found"));
        }
        User user = userOpt.get();

        Optional<SubAccount> subAccountOpt = subAccountRepository.findByUserIdAndCountryCode(user.getId(), user.getCountryCode());
        if(!subAccountOpt.isPresent()){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "No subaccount found"));
        }
        SubAccount subAccount = subAccountOpt.get();

        FundAccountRequestDto fundUserAccount = FundAccountRequestDto.builder()
                .amount(Double.parseDouble(request.getAmount()))
                .receiver(String.valueOf(subAccount.getSubAccountId()))
                .wallet("35d8776f-3708-4403-8d7d-b50a605777fd")
                .build();
        walletService.fundAccount(fundUserAccount);

        return ResponseEntity.ok().body(new FundWalletResponse(String.valueOf(HttpStatus.OK), "Successfully credited user account"));
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("mono-sec-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }


}
