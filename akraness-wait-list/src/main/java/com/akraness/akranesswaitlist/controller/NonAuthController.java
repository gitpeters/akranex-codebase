package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.dto.WaitListRequestDto;
import com.akraness.akranesswaitlist.service.IService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class NonAuthController {
    private final IService service;

    @PostMapping
    public ResponseEntity<Response> users(@RequestBody @Validated WaitListRequestDto requestDto) {
        return service.joinWaitList(requestDto);
    }

    @GetMapping
    public ResponseEntity<Response> getAllWaitingUsers() {
        return service.getAllWaitingUsers();
    }
}
