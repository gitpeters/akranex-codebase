package com.akraness.akranesswaitlist.plaid.controller;

import com.akraness.akranesswaitlist.plaid.dto.IdentityVerificationDto;
import com.akraness.akranesswaitlist.plaid.service.IdentificationVericationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/plaid/identity-verification")
@RequiredArgsConstructor
public class IdentityVerificationController {
    private final IdentificationVericationService identificationVericationService;

    @PostMapping("/create")
    public ResponseEntity<?> createIdentityVerification(@RequestBody IdentityVerificationDto request ) {
        return identificationVericationService.create(request);
    }

    @PostMapping("/get")
    public ResponseEntity<?> retriveIdentityVerification(@RequestBody IdentityVerificationDto request ) {
        return identificationVericationService.get(request);
    }

    @PostMapping("/list")
    public ResponseEntity<?> listIdentityVerification(@RequestBody IdentityVerificationDto request ) {
        return identificationVericationService.getList(request);
    }

    @PostMapping("/retry")
    public ResponseEntity<?> retryIdentityVerification(@RequestBody IdentityVerificationDto request ) {
        return identificationVericationService.retry(request);
    }
}
