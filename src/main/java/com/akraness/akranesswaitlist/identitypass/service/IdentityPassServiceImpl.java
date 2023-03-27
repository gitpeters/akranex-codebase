package com.akraness.akranesswaitlist.identitypass.service;


import com.akraness.akranesswaitlist.async.IdentityPassAsyncRunner;
import com.akraness.akranesswaitlist.config.CustomResponse;

import com.akraness.akranesswaitlist.entity.User;

import com.akraness.akranesswaitlist.identitypass.entity.DataSupportedCountry;
import com.akraness.akranesswaitlist.identitypass.repository.DataSupportedCountryRepository;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.util.KYCVericationStatus;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityPassServiceImpl implements IdentityPassService {
    private final IdentityPassAsyncRunner identityPassAsyncRunner;
    private final IUserRepository userRepository;
    private final DataSupportedCountryRepository dataSupportedCountryRepository;

    private final Utility utility;

    @Override
    public ResponseEntity<CustomResponse> validateAndProccessVerification(Map<String, Object> request) throws JsonProcessingException {
        String convertUserIdToString = String.valueOf(request.get("userId"));
        Long userId = Long.parseLong(convertUserIdToString);
        Optional<User> userObj = userRepository.findById(userId);
        if (!userObj.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomResponse("Failed", "User not found"));
        }
        User user = userObj.get();

        if (user.getKycStatus() == null || !user.getKycStatus().equalsIgnoreCase(KYCVericationStatus.VERIFIED.name())) {
            identityPassAsyncRunner.processKYCVerification(user, request);
            return ResponseEntity.ok().body(new CustomResponse("Verification in progress"));
        }
        if(user.getKycStatus().equalsIgnoreCase("VERIFIED")){
            return ResponseEntity.badRequest().body(new CustomResponse("User already verified"));
        }
        return null;
}

    @Override
    public void createPayload(Map<String, Object> payload) throws JsonProcessingException {
      identityPassAsyncRunner.setCountryPayload(payload);
    }

    @Override
    public Map<String, Object> getCountryDataPayload(String countryCode) throws JsonProcessingException {
        Map<String, Object> payload = null;
        Optional<DataSupportedCountry> dscObj = dataSupportedCountryRepository.findByCountryCode(countryCode);
        if(dscObj.isPresent()) {
            DataSupportedCountry dsc = dscObj.get();
            payload = new ObjectMapper().readValue(dsc.getPayload(), Map.class);
        }


        return payload;
    }

}
