package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chimoney/info")
@RequiredArgsConstructor
public class InfoCountroller {
    private final InfoService infoService;
    @GetMapping("/country-banks")
    private ResponseEntity<?> getCountryBanks(@RequestParam("countryCode") String countryCode) {
       return infoService.getCountryBanks(countryCode);
    }
}
