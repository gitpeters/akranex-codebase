package com.akraness.akranesswaitlist.plaid.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.plaid.dto.IdentityVerificationDto;
import org.springframework.http.ResponseEntity;

public interface IdentificationVericationService {
    ResponseEntity<CustomResponse> create(IdentityVerificationDto identityVerificationDto);
    ResponseEntity<CustomResponse> get(IdentityVerificationDto identityVerificationDto);
}
