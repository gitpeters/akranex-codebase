package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import org.springframework.http.ResponseEntity;

public interface SubAccountService {
    ResponseEntity<CustomResponse> createSubAccount(SubAccountRequestDto request);
    ResponseEntity<CustomResponse> getSubAccount(String subAccountId);
    ResponseEntity<CustomResponse> deleteSubAccount(String subAccountId);
}
