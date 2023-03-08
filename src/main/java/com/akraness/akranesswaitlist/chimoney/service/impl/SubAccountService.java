package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.dto.CustomResponse;
import com.akraness.akranesswaitlist.chimoney.dto.RestTemplateResponseErrorHandler;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.chimoney.request.SubAccountRequest;
import com.akraness.akranesswaitlist.chimoney.service.ISubAccountService;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SubAccountService implements ISubAccountService {
    private RestTemplate restTemplate;

    @Autowired
    ISubAccountRepository subAccountRepository;

    @Autowired
    Utility utility;

    @Value("${chimoney.api-key}")
    private String apiKey;

    @Value("${chimoney.base-url}")
    private String baseUrl;

    @Autowired
    public SubAccountService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Override
    public ResponseEntity<CustomResponse> createSubAccount(SubAccountRequest request) {
        if (utility.isNullOrEmpty(request.getEmail()))
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("email is required.").build());

        if (utility.isNullOrEmpty(request.getName()))
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("name is required.").build());

        if (request.getUserId() == null)
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("userId is required.").build());

        Optional<SubAccount> subAccount = subAccountRepository.findByUserId(request.getUserId());
        if(subAccount.isPresent())
            return ResponseEntity.badRequest().body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("sub account for this user already exists").build());

        Map<String, String> req = new HashMap<>();
        req.put("name", request.getName());
        req.put("email", request.getEmail());

        HttpEntity entity = new HttpEntity(req, this.headers());
        String url = baseUrl + "sub-account/create";
        ResponseEntity<CustomResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CustomResponse.class);

        if(response.getStatusCodeValue() == HttpStatus.OK.value()){
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, String> map = oMapper.convertValue(response.getBody().getData(), Map.class);
            SubAccount subacct = SubAccount.builder()
                    .subAccountId(map.get("id"))
                    .uid(map.get("uid"))
                    .userId(request.getUserId()).build();

            subAccountRepository.save(subacct);
        }

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<CustomResponse> getSubAccount(String subAccountId) {
        HttpEntity entity = new HttpEntity(this.headers());
        String url = baseUrl + "sub-account/get?id="+subAccountId;

        ResponseEntity<CustomResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, CustomResponse.class);

        return ResponseEntity.ok().body(response.getBody());

    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
