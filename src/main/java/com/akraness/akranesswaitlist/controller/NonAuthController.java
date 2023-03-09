package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.chimoney.request.SubAccountRequest;
import com.akraness.akranesswaitlist.chimoney.service.ISubAccountService;
import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.service.IService;
import com.akraness.akranesswaitlist.serviceimpl.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users/no-auth")
@RequiredArgsConstructor
@Slf4j
public class NonAuthController {
    private final IService service;
    private final INotificationService notificationService;
    private final AuthenticationService authenticationService;
    private final ISubAccountService subAccountService;

    @PostMapping("/join-waitlist")
    public ResponseEntity<Response> users(@RequestBody @Validated WaitListRequestDto requestDto) {
        return service.joinWaitList(requestDto);
    }

    @GetMapping
    public ResponseEntity<Response> getAllWaitingUsers() {
        return service.getAllWaitingUsers();
    }
    @GetMapping("/countries")
    public ResponseEntity<Response> getAllCountries() {
        return service.getAllCountries();
    }
//    @PostMapping("/kafka")
//    public ResponseEntity<Response> users(@RequestBody NotificationDto requestDto) throws JsonProcessingException {
//        notificationService.sendNotification(requestDto);
//        return ResponseEntity.ok(new Response("90000","Successful",null));
//    }
    @PostMapping("/pre-signup")
    public ResponseEntity<Response> preSignup(@RequestBody @Validated EmailVerificationRequestDto requestDto) throws JsonProcessingException {
        return service.preSignUp(requestDto);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Response> verifyEmail(@RequestBody @Validated EmailVerificationDto requestDto) throws JsonProcessingException {
        return service.verifyEmail(requestDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<Response> Signup(@RequestBody @Validated SignupRequestDto requestDto) throws JsonProcessingException {
        return service.signup(requestDto);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(@RequestBody @Validated LoginRequestDto requestDto) throws Exception {
        return authenticationService.createAuthenticationToken(requestDto);
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<Response> verifyPhone(@RequestBody @Validated PhoneVerificationDto requestDto) throws JsonProcessingException {
        return service.verifyPhone(requestDto);
    }

    @PostMapping("/create-magic-pin")
    public ResponseEntity<Response> createMagicPin(@RequestBody @Validated MagicPinRequestDto requestDto) {
        return service.createMagicPin(requestDto);
    }

    @PostMapping("/resend-phone-otp")
    public ResponseEntity<Response> resendPhoneOtpCode(@RequestBody @Validated ResendPhoneOtpRequest requestDto) throws JsonProcessingException {
        return service.resendPhoneOtpCode(requestDto);
    }

    @PostMapping("/create-sub-account")
    public ResponseEntity<?> createSubAccount(@RequestBody @Validated SubAccountRequest request ) {
        return subAccountService.createSubAccount(request);
    }

    @GetMapping("/get-sub-account")
    public ResponseEntity<?> getSubAccount(@RequestParam("id") String subAccountId) {
        return subAccountService.getSubAccount(subAccountId);
    }

    @GetMapping("/get-countries")
    public ResponseEntity<?> getCountries() {
        return service.getCountries();
    }
}
