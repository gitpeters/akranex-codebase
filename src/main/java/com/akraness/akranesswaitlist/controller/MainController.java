package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.dto.MagicPinRequestDto;
import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.dto.VerifyPinRequestDto;
import com.akraness.akranesswaitlist.dto.WaitListRequestDto;
import com.akraness.akranesswaitlist.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final IService service;

    @PostMapping("/verify-pin")
    public ResponseEntity<Response> verifyPin(@RequestBody @Validated VerifyPinRequestDto requestDto) {
        return service.verifyPin(requestDto);
    }
}
