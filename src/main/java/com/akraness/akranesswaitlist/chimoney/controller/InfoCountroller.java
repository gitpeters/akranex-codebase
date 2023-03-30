package com.akraness.akranesswaitlist.chimoney.controller;

import com.akraness.akranesswaitlist.chimoney.dto.BankWrapperDto;
import com.akraness.akranesswaitlist.chimoney.service.InfoService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.entity.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chimoney/info")
@RequiredArgsConstructor
public class InfoCountroller {
    private final InfoService infoService;
    @GetMapping("/country-banks")
    public ResponseEntity<?> getCountryBanks(@RequestParam("countryCode") String countryCode) {
       return infoService.getCountryBanks(countryCode);
    }

    @PostMapping("/verify-bank-account-number")
    public ResponseEntity<?> verifyBankAccountNumber(@RequestBody BankWrapperDto wrapperDto) {
        return infoService.verifyBankAccountNumber(wrapperDto);
    }

    @GetMapping("/airtime-countries")
    public ResponseEntity<?> airtimeCountries() throws JsonProcessingException {
        List<Country> countryList = infoService.airtimeCountries();
        CustomResponse response = CustomResponse.builder()
                .status("Airtime Countries")
                .data(countryList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
