package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.request.SubAccountRequest;
import org.springframework.http.ResponseEntity;

public interface ISubAccountService {
    ResponseEntity<CustomResponse> createSubAccount(SubAccountRequest request);
    ResponseEntity<CustomResponse> getSubAccount(String subAccountId);
}
