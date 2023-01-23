package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class NonAuthController {
    private final IService service;
    private final INotificationService notificationService;

    @PostMapping
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
}
