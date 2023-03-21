package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.Async.IdentityPassAsyncRunner;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.entity.DataSupportedCountry;
import com.akraness.akranesswaitlist.identitypass.repository.DataSupportedCountryRepository;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public ResponseEntity<CustomResponse> validateAndProccessVerification(Map<String, Object> request) {
        Optional <User> userObj = userRepository.findById(1l);
        if(!userObj.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomResponse("Failed", "User not found"));
        }

        //check if type is not doc or data, then return error
        identityPassAsyncRunner.processKYCVerification(userObj.get(), request);

        return ResponseEntity.ok().body(new CustomResponse("Verification in progress"));

    }

    @Override
    public void createPayload(Map<String, Object> payload) throws JsonProcessingException {
      identityPassAsyncRunner.setCountryPayload(payload);
    }

    @Override
    public ResponseEntity<?> getCountryDataPayload(String countryCode) throws JsonProcessingException {
        Optional<DataSupportedCountry> dscObj = dataSupportedCountryRepository.findByCountryCode(countryCode);
        if(!dscObj.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomResponse("Failed", "Country not found"));
        }

        DataSupportedCountry dsc = dscObj.get();

        Map<String, Object> payload = new ObjectMapper().readValue(dsc.getPayload(), Map.class);

        return ResponseEntity.ok().body(payload);
    }

}
