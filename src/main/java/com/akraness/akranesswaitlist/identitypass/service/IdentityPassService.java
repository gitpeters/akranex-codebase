package com.akraness.akranesswaitlist.identitypass.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequest;
import org.springframework.http.ResponseEntity;

public interface IdentityPassService {
    ResponseEntity<CustomResponse> validateBvn(IdentityPassRequest request);
}
