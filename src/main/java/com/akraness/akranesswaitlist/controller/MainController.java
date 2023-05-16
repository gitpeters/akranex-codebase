package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.concurrent.ExecutionException;

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

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUser(@PathVariable("id") long userId) throws JsonProcessingException {
        return service.getUser(userId);
    }

    @PostMapping("/edit-pin")
    public ResponseEntity<Response> editPin(@RequestBody @Validated EditPinRequestDto requestDto) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        return service.editPin(requestDto);
    }
}
