package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface SubAccountService {
    ResponseEntity<CustomResponse> createSubAccount(SubAccountRequestDto request);
    ResponseEntity<?> getSubAccount(String subAccountId, String countryCode) throws JsonProcessingException;
    ResponseEntity<CustomResponse> deleteSubAccount(String subAccountId);
}
