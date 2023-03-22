package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.dto.SubAccountRequestDto;
import com.akraness.akranesswaitlist.chimoney.service.SubAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chimoney/subaccout")
@RequiredArgsConstructor
public class SubAccountController {
    private final SubAccountService subAccountService;

    @PostMapping("/create")
    public ResponseEntity<?> createSubAccount(@RequestBody SubAccountRequestDto request ) {
        return subAccountService.createSubAccount(request);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getSubAccount(@RequestParam("id") String subAccountId, @RequestParam("countryCode") String countryCode) throws JsonProcessingException {
        return subAccountService.getSubAccount(subAccountId, countryCode);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSubAccount(@RequestParam("id") String subAccountId) {
        return subAccountService.deleteSubAccount(subAccountId);
    }
}
