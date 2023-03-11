package com.akraness.akranesswaitlist.plaid.service.impl;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.plaid.dto.IdentityVerificationDto;
import com.akraness.akranesswaitlist.plaid.service.IdentificationVericationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentificationVericationServiceImpl implements IdentificationVericationService {
    private final RestTemplateService restTemplateService;

    @Value("${plaid.base-url}")
    private String baseUrl;
    @Value("${plaid.template-id}")
    private String templateId;
    @Value("${plaid.client-id}")
    private String clientId;
    @Value("${plaid.secret}")
    private String secret;

    @Override
    public ResponseEntity<CustomResponse> create(IdentityVerificationDto identityVerificationDto) {
        identityVerificationDto.setTemplate_id(templateId);
        identityVerificationDto.setClient_id(clientId);
        identityVerificationDto.setSecret(secret);
        String url = baseUrl + "identity_verification/create";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, identityVerificationDto, null);
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> get(IdentityVerificationDto identityVerificationDto) {
        identityVerificationDto.setClient_id(clientId);
        identityVerificationDto.setSecret(secret);
        String url = baseUrl + "identity_verification/get";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, identityVerificationDto, null);
        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> getList(IdentityVerificationDto identityVerificationDto) {
        identityVerificationDto.setTemplate_id(templateId);
        identityVerificationDto.setClient_id(clientId);
        identityVerificationDto.setSecret(secret);
        String url = baseUrl + "identity_verification/list";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, identityVerificationDto, null);
        return ResponseEntity.ok().body(response.getBody());
    }
}
