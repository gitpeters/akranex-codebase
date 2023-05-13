package com.akraness.akranesswaitlist.fundwallet.service;

import com.akraness.akranesswaitlist.barter.service.CurrencyConverterService;
import com.akraness.akranesswaitlist.chimoney.dto.FundAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.chimoney.service.WalletService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.Referral;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.enums.ReferralStatus;
import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletRequest;
import com.akraness.akranesswaitlist.fundwallet.dto.FundWalletResponse;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.repository.ReferralRepository;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FundWalletServiceImpl implements FundWalletService{

    private final RestTemplateService restTemplateService;
    private final WalletService walletService;
    private final IUserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final ISubAccountRepository subAccountRepository;
    private final CurrencyConverterService converterService;
    @Value("${mono.api-key}")
    private String apiKey;
    @Value("${mono.base-url}")
    private String baseUrl;
    @Value("${mono.reference}")
    private String referenceID;

    private final Utility utility;

    @Override
    public ResponseEntity<?> fundWallet(FundWalletRequest request, String akranexTag) throws JsonProcessingException {

        Map<String, String> fundWalletRequest = new HashMap<>();

        if(utility.isNullOrEmpty(String.valueOf(request.getAmount()))){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "amount is require"));
        }
        if(utility.isNullOrEmpty(akranexTag)){
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "akranexTag is require"));
        }


        double amount = request.getAmount() * 100;

        fundWalletRequest.put("amount", String.valueOf(amount));
        fundWalletRequest.put("type", "onetime-debit");
        fundWalletRequest.put("description", request.getDescription());
        fundWalletRequest.put("reference", referenceID);

        String url = baseUrl+"payments/initiate";


        ResponseEntity<CustomResponse> response = restTemplateService.post(url, fundWalletRequest, headers());
        Map<String, String> responseData = new ObjectMapper().convertValue(response.getBody(), Map.class);
        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {
            Map<String, String> verifyPaymentStatus = new HashMap<>();
            verifyPaymentStatus.put("reference", referenceID);
            ResponseEntity<CustomResponse> verifyResponse = restTemplateService.post(baseUrl+"payments/verify", verifyPaymentStatus, headers());
            Map<String, String> verifyResponseData = new ObjectMapper().convertValue(verifyResponse.getBody().getData(), Map.class);
            if(verifyResponse.getStatusCodeValue() == HttpStatus.OK.value()){
                fundUserAccount(request, akranexTag);
                double fundedAmount = Double.parseDouble(responseData.get("amount"))/100;
                return ResponseEntity.ok().body(

                        FundWalletResponse.builder()
                                .amount(fundedAmount)
                                .reference(responseData.get("reference"))
                                .description(responseData.get("description"))
                                .status("Successful")
                                .message("Wallet funded successfully")
                                .transactionDate(responseData.get("updated_at"))
                                .build()
                );
            }
            return ResponseEntity.badRequest().body(
                    FundWalletResponse.builder()
                            .amount(request.getAmount())
                            .status(verifyResponseData.get("status"))
                            .message(verifyResponseData.get("message"))
                            .build()
            );
        }
        return ResponseEntity.badRequest().body(
                FundWalletResponse.builder()
                        .amount(request.getAmount())
                        .status(responseData.get("status"))
                        .message(responseData.get("message"))
                        .build()
        );
    }

    private ResponseEntity<?> fundUserAccount(FundWalletRequest request, String akranexTag) throws JsonProcessingException {
        Optional<User> userOpt = userRepository.findByAkranexTag(akranexTag);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "No user found"));
        }
        User user = userOpt.get();

        Optional<SubAccount> subAccountOpt = subAccountRepository.findByUserIdAndCountryCode(user.getId(), user.getCountryCode());
        if (!subAccountOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new FundWalletResponse(String.valueOf(HttpStatus.BAD_REQUEST), "No subaccount found"));
        }
        double amount = converterService.convertToUSD(Currency.getInstance(request.getCurrencyCode()), request.getAmount());
        SubAccount subAccount = subAccountOpt.get();

        FundAccountRequestDto fundUserAccount = FundAccountRequestDto.builder()
                .amount(amount)
                .receiver(String.valueOf(subAccount.getSubAccountId()))
                .wallet("chi")
                .build();
        walletService.fundAccount(fundUserAccount);

        var referralOptional = referralRepository.findByNewUserId(user.getId());

        // Update referral table once a new user funded his/her account with a minimum of 5 USD
        if (referralOptional.isPresent() && amount >= 5.0) {
            Referral userReferral = Referral.builder()
                    .newUserFundedAmount(amount)
                    .referralRewardStatus(String.valueOf(ReferralStatus.PAID))
                    .build();
            referralRepository.save(userReferral);
        }

            return ResponseEntity.ok().body(new FundWalletResponse(String.valueOf(HttpStatus.OK), "Successfully credited user account"));


    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("mono-sec-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }


}
